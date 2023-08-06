package store.management.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import store.management.dto.*;
import store.management.entities.*;
import store.management.exception.ValidationException;
import store.management.repositories.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerOrderedProductsService {
	private final EmailNotificationService emailNotificationService;
	private final SupplierService supplierService;
	private final DistributorService distributorService;
	private final ProductService productService;
	private final CustomerOrderedProductsRepository customerOrderedProductsRepository;
	private final CustomerOrderRepository customerOrderRepository;
	private final UserRepository userRepository;
	private final DistributorProductRepository distributorProductRepository;
	private final SupplierProductRepository supplierProductRepository;
	@Value("${minimum.product.quantity}")
	private int minimumQuantity;
	@Value("${supplier.product.quantity}")
	private int supplierQuantityToOrder;
	
	public CustomerOrderDto addToCart(String email, long productId, int quantity) throws Exception {
		User user = userRepository.findByUsername(email);
		Product product = productService.findProductById(productId);
		DistributorProduct distributorProduct =
				distributorProductRepository.findByDistributorIdAndProductId(user.getDistributor().getDistributorId(),
						productId);
		int availableQuantity = distributorProduct.getQuantity();
		if(availableQuantity < quantity) {
			boolean orderPlaced = orderFromSupplier(productId, quantity, distributorProduct);
			if(!orderPlaced) {
				throw new ValidationException("Failed to add in cart, (Out of Stock)");
			}
		}
		CustomerOrderDto customerOrderDto = getCustomerOrder(email, quantity, product, user);
		distributorProduct.setQuantity(distributorProduct.getQuantity() - quantity);
		distributorProductRepository.saveAndFlush(distributorProduct);
		if(distributorProduct.getQuantity() < minimumQuantity) {
			boolean orderPlaced = orderFromSupplier(productId, supplierQuantityToOrder, distributorProduct);
			if(!orderPlaced) {
				throw new ValidationException("Failed to add in cart, (Out of Stock)");
			}
		}
		return customerOrderDto;
	}

	public List<CustomerOrderDto> getOrders(String email, String type, long customerOrderId) {
		List<CustomerOrder> customerOrderList = new ArrayList();
		User user = userRepository.findByUsername(email);
		if(type.equalsIgnoreCase("cart")) {
			CustomerOrder customerOrder = customerOrderRepository.findByUserAndStatus(user,"Added To Cart");
			if(customerOrder != null) {
				customerOrderList.add(customerOrder);
			}
		} else if(type.equalsIgnoreCase("ordered")) {
			List<String> statuses = new ArrayList<>();
			statuses.add("Added To Cart");
			if(customerOrderId != 0) {
				CustomerOrder customerOrder = customerOrderRepository.findByCustomerOrderId(customerOrderId);
				if(customerOrder != null && customerOrder.getStatus().equalsIgnoreCase("Ordered")) {
					customerOrderList.add(customerOrder);
				}
			} else {
				Set<Role> roles = user.getRoles();
				boolean isCustomerRepresentative = false;
				for(Role role : roles) {
					if(role.getRoleName().equals("Customer_Representative")) {
						isCustomerRepresentative = true;
						break;
					}
				}
				if(isCustomerRepresentative) {
					customerOrderList = customerOrderRepository.findByStatusNotIn(statuses);
				} else {
					customerOrderList =
							customerOrderRepository.findByUserAndStatusNotIn(user, statuses);
				}
			}
		}
		List<CustomerOrderDto> customerOrderDtos = new ArrayList<>();
		for(CustomerOrder customerOrder : customerOrderList) {
			customerOrderDtos.add(getCustomerOrderDto(customerOrder));
		}
		Collections.reverse(customerOrderDtos);
		return customerOrderDtos;
	}
	public CustomerOrder removeFromCart(long orderedProductId, long customerOrderId) {
		CustomerOrderedProduct customerOrderedProduct = customerOrderedProductsRepository.findById(orderedProductId).get();
		User user = userRepository.findByUsername(customerOrderedProduct.getEmail());
		Product product = customerOrderedProduct.getProduct();
		DistributorProduct distributorProduct =
				distributorProductRepository.findByDistributorIdAndProductId(user.getDistributor().getDistributorId(),
						product.getProductId());
		distributorProduct.setQuantity(distributorProduct.getQuantity() + customerOrderedProduct.getQuantity());
		distributorProductRepository.saveAndFlush(distributorProduct);
		CustomerOrder customerOrder = customerOrderRepository.findById(customerOrderId).get();
		List<CustomerOrderedProduct> orderedProductModelList = customerOrderedProductsRepository.findByCustomerOrder(
				customerOrder);
		if(orderedProductModelList.size() == 1) {
			try {
				customerOrderedProductsRepository.deleteById(orderedProductId);
				customerOrderRepository.deleteById(customerOrderId);
			}catch (Exception e) {
				log.error("Exception occurred while removing product from cart");
			}
		} else {
			customerOrderedProductsRepository.deleteById(orderedProductId);
		}		
		return customerOrder;
	}
	public CustomerOrder setCustomerOrderStatus(long customerOrderId, String status, String customerName,
										  String customerPhone) {
		CustomerOrder customerOrder = customerOrderRepository.findById(customerOrderId).get();
		customerOrder.setStatus(status);
		customerOrder.setDate("" + new Date());
		if(status.equalsIgnoreCase("Ordered")) {
			customerOrder.setCustomerName(customerName);
			customerOrder.setCustomerPhone(customerPhone);
			customerOrder.setStatus(status);
		}
		customerOrderRepository.saveAndFlush(customerOrder);
		return customerOrder;
	}

	public CustomerOrder returnItem(long orderedProductId, long customerOrderId, int quantity) {
		CustomerOrder customerOrder = customerOrderRepository.findById(customerOrderId).get();
		boolean allReturned = true;
		Product product = null;
		String email = "";
		for(CustomerOrderedProduct customerOrderedProduct : customerOrder.getCustomerOrderedProductList()) {
			if(customerOrderedProduct.getCustomerOrderedProductId() == orderedProductId) {
				product = customerOrderedProduct.getProduct();
				email = customerOrderedProduct.getEmail();
				if(customerOrderedProduct.getQuantity() == quantity) {
					customerOrderedProduct.setStatus("Returned");
				} else {
					allReturned = false;
				}
				customerOrderedProduct.setQuantity(customerOrderedProduct.getQuantity() - quantity);
			} else if(!customerOrderedProduct.getStatus().equalsIgnoreCase("Returned")){
				allReturned = false;
			}
		}
		if(allReturned) {
			customerOrder.setStatus("Returned");
		}
		customerOrderRepository.save(customerOrder);
		User user = userRepository.findByUsername(email);
		DistributorProduct distributorProduct =
				distributorProductRepository.findByDistributorIdAndProductId(user.getDistributor().getDistributorId(),
						product.getProductId());
		distributorProduct.setQuantity(distributorProduct.getQuantity() + quantity);
		distributorProductRepository.saveAndFlush(distributorProduct);
		return customerOrder;
	}

	private boolean orderFromSupplier(long productId, int quantity, DistributorProduct distributorProduct) {
		List<SupplierProduct> supplierProducts = supplierProductRepository.findByProductId(productId);
		Map<Long, SupplierProduct> supplierModelMap =
				supplierProducts.stream().collect(Collectors.toMap(SupplierProduct::getSupplierProductId,
						Function.identity()));
		int totalStock = 0;
		if(CollectionUtils.isNotEmpty(supplierProducts)) {
			Map<Long, Integer> supplierQuantities = new HashMap<>();
			for (SupplierProduct supplierProduct : supplierProducts) {
				totalStock += supplierProduct.getQuantity();
				supplierQuantities.put(supplierProduct.getSupplierProductId(), supplierProduct.getQuantity());
			}
			Map<Long, Integer> sortedMap = sortByValue(supplierQuantities);
			for(Map.Entry<Long, Integer> entry : sortedMap.entrySet()) {
				if(entry.getValue() > quantity) {
					updatedSupplierModels(supplierModelMap, entry, quantity, distributorProduct);
					return true;
				} else if(totalStock > quantity){
					int availableQuantity = entry.getValue();
					updatedSupplierModels(supplierModelMap, entry, availableQuantity, distributorProduct);
					quantity -= availableQuantity;
				} else {
					sendMailToSuppliersAndDistributors(supplierProducts, distributorProduct);
					return false;
				}
			}
		}
		return false;
	}

	private void sendMailToSuppliersAndDistributors(List<SupplierProduct> supplierProducts, DistributorProduct distributorProduct) {
		List<Long> supplierIds = supplierProducts.stream().map(s -> s.getSupplierId()).collect(Collectors.toList());
		Product product = productService.findProductById(supplierProducts.get(0).getProductId());
		List<Supplier> suppliers = supplierService.fetchAllByIds(supplierIds);
		for(Supplier supplier : suppliers) {
			emailNotificationService.sendEmail(supplier.getEmail(), "Product Out Of Stock", product.getProductName() +
																							" is out of stock in your inventory");
		}
		Distributor distributor = distributorService.fetchById(distributorProduct.getDistributorId());
		emailNotificationService.sendEmail(distributor.getEmail(), "Product Out Of Stock", product.getProductName() +
																						   " is out of stock in your inventory");
	}

	private CustomerOrderedProduct createOrUpdateCustomerOrderedProduct(String email, int quantity, Product product,
													  CustomerOrder customerOrder) {
		CustomerOrderedProduct customerOrderedProduct =
				customerOrderedProductsRepository.findByCustomerOrderAndProduct(customerOrder, product);
		if(isNull(customerOrderedProduct)) {
			customerOrderedProduct = new CustomerOrderedProduct();
			customerOrderedProduct.setCustomerOrder(customerOrder);
			customerOrderedProduct.setProduct(product);
			customerOrderedProduct.setQuantity(quantity);
			customerOrderedProduct.setOrderDate("" + new Date());
			customerOrderedProduct.setStatus("Added To Cart");
			customerOrderedProduct.setPrice(product.getPrice());
			customerOrderedProduct.setEmail(email);
		} else {
			customerOrderedProduct.setQuantity(customerOrderedProduct.getQuantity() + quantity);
			customerOrderedProduct.setOrderDate("" + new Date());
		}
		return customerOrderedProductsRepository.saveAndFlush(customerOrderedProduct);
	}

	private CustomerOrderDto getCustomerOrder(String email, int quantity, Product product, User user) {
		CustomerOrder customerOrder = customerOrderRepository.findByUserAndStatus(user,"Added To Cart");
		if(isNull(customerOrder)) {
			customerOrder = new CustomerOrder();
			customerOrder.setUser(user);
			customerOrder.setDate("" + new Date());
			customerOrder.setStatus("Added To Cart");
			customerOrder = customerOrderRepository.save(customerOrder);
		}
		CustomerOrderedProduct customerOrderedProduct = createOrUpdateCustomerOrderedProduct(email, quantity, product,
				customerOrder);
		List<CustomerOrderedProduct> customerOrderedProductList =
				customerOrder.getCustomerOrderedProductList();
		if(CollectionUtils.isEmpty(customerOrderedProductList)) {
			customerOrderedProductList = new ArrayList<>();
		}
		customerOrderedProductList.add(customerOrderedProduct);
		customerOrder.setCustomerOrderedProductList(customerOrderedProductList);
		return getCustomerOrderDto(customerOrderRepository.save(customerOrder));
	}

	private void updatedSupplierModels(Map<Long, SupplierProduct> supplierProductModelMap,
									   Map.Entry<Long,	Integer> entry,
									   int availableQuantity,
									   DistributorProduct distributorProduct) {
		SupplierProduct supplierProduct = supplierProductModelMap.get(entry.getKey());
		supplierProduct.setQuantity(supplierProduct.getQuantity() - availableQuantity);
		supplierProductRepository.saveAndFlush(supplierProduct);
		distributorProduct.setQuantity(distributorProduct.getQuantity() + availableQuantity);
		distributorProductRepository.saveAndFlush(distributorProduct);
	}

	private HashMap<Long, Integer> sortByValue(Map<Long, Integer> hm)
	{
		HashMap<Long, Integer> temp
				= hm.entrySet()
					.stream()
					.sorted((i2, i1)
							-> i1.getValue().compareTo(
							i2.getValue()))
					.collect(Collectors.toMap(
							Map.Entry::getKey,
							Map.Entry::getValue,
							(e1, e2) -> e1, LinkedHashMap::new));
		return temp;
	}

	private CustomerOrderDto getCustomerOrderDto(CustomerOrder customerOrder) {
		CustomerOrderDto customerOrderDto = new CustomerOrderDto();
		customerOrderDto.setCustomerOrderId(customerOrder.getCustomerOrderId());
		customerOrderDto.setCustomerOrderedProductList(getCustomerOrderedProductList(customerOrder.getCustomerOrderedProductList()));
		customerOrderDto.setCustomerName(customerOrder.getCustomerPhone());
		customerOrderDto.setCustomerPhone(customerOrder.getCustomerPhone());
		customerOrderDto.setDate(customerOrder.getDate());
		customerOrderDto.setUser(getUserDto(customerOrder.getUser()));
		customerOrderDto.setStatus(customerOrder.getStatus());
		return customerOrderDto;
	}

	private UserDto getUserDto(User user) {
		UserDto userDto = new UserDto();
		userDto.setName(user.getName());
		userDto.setPhone(user.getPhone());
		userDto.setAddress(user.getAddress());
		userDto.setEmail(user.getUsername());
		RoleDto roleDto = new RoleDto();
		for(Role role : user.getRoles()) {
			roleDto.setRoleName(role.getRoleName());
			roleDto.setRoleId(role.getRoleId());
		}
		userDto.setRole(roleDto);
		return userDto;
	}

	private List<CustomerOrderedProductDto> getCustomerOrderedProductList(List<CustomerOrderedProduct> customerOrderedProducts) {
		List<CustomerOrderedProductDto> customerOrderedProductDtos = new ArrayList<>();
		for(CustomerOrderedProduct customerOrderedProduct : customerOrderedProducts) {
			CustomerOrderedProductDto customerOrderedProductDto = new CustomerOrderedProductDto();
			customerOrderedProductDto.setCustomerOrderedProductId(customerOrderedProduct.getCustomerOrderedProductId());
			customerOrderedProductDto.setCustomerOrderId(customerOrderedProduct.getCustomerOrderId());
//			customerOrderedProductDto.setCustomerOrder(customerOrderedProduct.getCustomerOrder());
			customerOrderedProductDto.setProductId(customerOrderedProduct.getProductId());
			customerOrderedProductDto.setProduct(getProductDto(customerOrderedProduct.getProduct()));
			customerOrderedProductDto.setEmail(customerOrderedProduct.getEmail());
			customerOrderedProductDto.setOrderDate(customerOrderedProduct.getOrderDate());
			customerOrderedProductDto.setQuantity(customerOrderedProduct.getQuantity());
			customerOrderedProductDto.setPrice(customerOrderedProduct.getPrice());
			customerOrderedProductDto.setStatus(customerOrderedProduct.getStatus());
			customerOrderedProductDtos.add(customerOrderedProductDto);
		}
		return customerOrderedProductDtos;
	}

	private ProductDto getProductDto(Product product1) {
		ProductDto productDto = new ProductDto();
		Product product = productService.findProductById(product1.getProductId());
		productDto.setProductCode(product.getProductCode());
		productDto.setProductName(product.getProductName());
		productDto.setDescription(product.getDescription());
		productDto.setProductId(product.getProductId());
		productDto.setCategoryId(product.getCategory().getCategoryId());
		productDto.setPictureName(product.getPictureName());
		productDto.setPrice(product.getPrice());
		productDto.setSubCategoryId(product.getSubCategory().getSubCategoryId());
		productDto.setCategoryName(product.getCategory().getCategoryName());
		productDto.setSubCategoryName(product.getSubCategory().getSubCategoryName());
		return productDto;
	}
}
