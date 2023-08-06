import React, { useEffect, useRef, useState } from 'react'
import axios from 'axios'
import useInput from '../../hooks/useInput'
import { useHistory } from "react-router-dom";
import Cookies from 'js-cookie'
const rest = require('../../BackEndApis')
function AddToCart() {
    let total = 0;
    const [productName, setProductName,  bindProductName, resetProductName] = useInput('')
    const [productCode, setProductCode,  bindProductCode, resetProductCode] = useInput('')
    const [locations, setLocations] = useState([])
    const [products, setProducts] = useState([])
    const [categoryId, setCategoryId,  bindCategoryId, resetCategoryId] = useInput('')
    const [subCategoryId, setSubCategoryId,  bindSubCategoryId, resetSubCategoryId] = useInput('')
    const [categories, setCategories] = useState([])
    const [subCategories, setSubCategories] = useState([])
    const [role, setRole] = useState('')
    const [count, setCount] = useState(0);

    const [customerOrders, setCustomerOrders] = useState([])
    const [customerName, setCustomerName,  bindCustomerName, resetCustomerName] = useInput('')
    const [customerPhone, setCustomerPhone,  bindCustomerPhone, resetCustomerPhone] = useInput('')
    const [customerOrderId, setCustomerOrderId,  bindCustomerOrderId, resetCustomerOrderId] = useInput('')

    const header = {
        headers: {
            "Content-type": "Application/json",
            "Authorization": `Bearer ${Cookies.get('token')}`
        }
    }
    useEffect(() => {
        console.log('insixe***********')
        axios.get(rest.endPointProductOrders+"?type=cart&customerOrderId="+customerOrderId,header)
            .then(response => {
                console.log(response.data.data);
                setCustomerOrders(response.data.data)
            })
            .catch(err => {
                console.log(err)
            })
    }, [count]);
    useEffect(() => {
        console.log(Cookies.get('role'));
        setRole(Cookies.get('role'))
        axios.get(rest.endPointCategory + "all",header)
            .then(response => {
                console.log(response.data.data);
                setCategories(response.data.data)
            })
            .catch(err => {
                console.log(err)
            })
    }, [count]);
    const getSubCategories = (e)=>{
        setCategoryId(e.target.value)
        axios.get(rest.endPointSubCategory+"/?categoryId="+e.target.value, header)
        .then(response => {
            console.log(response.data.data);
            setSubCategories(response.data.data)
        })
        .catch(err => {
            console.log(err)
        })
        submitHandlerSearch(e)
    }
    useEffect(() => {
        let url = rest.endPointSearchProduct;
        console.log(url)
        let data = {
            "categoryId":parseInt(categoryId),
            "subCategoryId":parseInt(subCategoryId),
            "productName":productName,
            "productCode":productCode
        }

        axios.post(rest.endPointSearchProduct,data,header)
        .then(response => {
            console.log(response.data.data);
            setProducts(response.data.data)
        })
        .catch((err) => {
            console.log(err)
        })
    }, [count]);
    const submitHandlerUpdateQuantity = (e) => {
        e.preventDefault()
        console.log(e);
        let productId = e.target[0].value;
        let quantity = e.target[1].value;
            axios.get(rest.endPointProductUpdate+"?productId="+productId+"&quantity="+quantity,header)
                .then(response => {
                   setCount(count+1)
                   e.target[1].value = ''
            })
            .catch(err => {
                    console.log(err)
            })
        console.log(productId+" "+quantity);
    }
    const submitHandlerCardToCart = (e) => {
        e.preventDefault()
        console.log(e);
        let productId = e.target[0].value;
        let available = e.target[1].value;
        let quantity = e.target[2].value;
        if(parseInt(available)<parseInt(quantity)){
            alert("Only "+available+" Products Available")
            e.target[1].value = ''+available
        }else {
            axios.get(rest.endPointProductAddToCart+"?productId="+productId+"&quantity="+quantity,header)
            .then(response => {
               alert(quantity + " products added to cart successfully")
               setCount(count+1)
               e.target[1].value = ''
            })
            .catch(err => {
                    console.log(err)
            })
        }

        console.log(productId+" "+quantity);
    }
    const submitHandlerSearch= (e) => {
        e.preventDefault()
        setProductCode(document.getElementById('productCode').value)
        setProductName(document.getElementById('productName').value)
        setCategoryId(document.getElementById('categoryId').value)
        setSubCategoryId(document.getElementById('subCategoryId').value)
        setCount(count+1)
    }
    const submitHandlerRemoveFromCart= (e) => {
        e.preventDefault()
        console.log(e);
        let orderedProductId = e.target[0].value;
        let customerOrderId= e.target[1].value;
            axios.get(rest.endPointProductRemoveFromCart+"?orderedProductId="+orderedProductId+"&customerOrderId="+customerOrderId,header)
                .then(response => {
                   setCount(count+1)
                   e.target[1].value = ''
            })
            .catch(err => {
                    console.log(err)
            })
    }
    
    const submitHandleSetStatus= (e) => {
        e.preventDefault()
        console.log(e);
        let customerOrderId = e.target[0].value;
        let status= e.target[1].value;
        console.log(customerOrderId)
        console.log(status)
            axios.get(rest.endPointsetStatus+"?customerOrderId="+customerOrderId+"&status="+status+"&customerName="+customerName+"&customerPhone="+customerPhone,header)
                .then(response => {
                   setCount(count+1)
                   alert("Order has been placed successfully")
                   e.target[1].value = ''
            })
            .catch(err => {
                    console.log(err)
            })
    }

    return (
        <div style={{backgroundColor:'#2c3e50'}}>
        <div className="container" >
            <form onSubmit={submitHandlerSearch} >
            <div className='row ps-3 pe-3'>
            <div className='col-md-3'>
                    <div className=''>
                        <div className="">
                            <label for="productCode" className="form-label text-white">Product Code</label>
                            <input type='text' id='productCode'  className="form-control" {...bindProductCode}  placeholder="Enter Product Code" onKeyUp={submitHandlerSearch}/>
                        </div>
                    </div>
                </div>
                <div className='col-md-3'>
                    <div className=''>
                        <div className="">
                            <label for="productName" className="form-label text-white">Product Name</label>
                            <input type='text'  id='productName' className="form-control" {...bindProductName}  placeholder="Enter Product Name" onKeyUp={submitHandlerSearch}/>
                        </div>
                    </div>
                </div>
                <div className='col-md-3'>
                <div class="">
                    <label for="categoryName" class="form-label text-white">Category</label>
                        <select className='form-control' id='categoryId'  onChange={getSubCategories}>
                            <option value="">Choose Category</option>
                            {categories.map((category, index) =>
                                <option className="list-group-item" key={category.categoryId} value={category.categoryId}>
                                {category.categoryName}
                            </option>)}
                        </select>
                </div>
                </div>
                <div className='col-md-3'>
                        <div className="">
                            <label for="s" class="form-label text-white">Sub Category</label>
                                <select className='form-control' id='subCategoryId' {...bindSubCategoryId} onChange={submitHandlerSearch}>
                                    <option value="">Choose Sub Category</option>
                                    {subCategories.map((subCategory, index) =>
                                        <option className="list-group-item" key={subCategory.subCategoryId} value={subCategory.subCategoryId}>
                                        {subCategory.subCategoryName}
                                    </option>)}
                                </select>
                        </div>
                </div>
            </div>
            </form>
            <div className='card mt-2 ps-4 pe-4 pt-2 pb-2' style={{height:'150px',overflow:'auto'}}>
                {products.map((product, index) =>
                <div  key={product.productId}>
                <div className="row">
                    <div className="col-md-3">{product['productCode']}</div>
                    <div className="col-md-3">{product['productName']}</div>
                    <div className="col-md-3">$ {product['price']}</div>
                    <div className="col-md-3">
                        <form onSubmit={submitHandlerCardToCart}>
                        <input type="hidden" name="productId" value={product.productId}/>
                        <input type="hidden" name="availableQuantity" value={product.availableQuantity}/>
                            <div className='row'>
                                <div className='col-md-6'>
                                    <input type='number' min="1" max={product.availableQuantity}  className="form-control" placeholder="Quantity" required/>
                                </div>
                                <div className='col-md-6'>
                                    <button className='btn btn-primary w-100'>Add</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>  
                <hr style={{margin:'3px'}}/>
                </div>
                )}
            </div>
        </div>
        <div className='container'>
            {customerOrders.map((customerOrder, index) =>
                <div key={customerOrder.customerOrderId}>
                    <div className="card mt-3" >
                        <div className="card-header">
                            <div className='row'>
                            <div className='col-md-2'>
                                <div>
                                    <div className='text-muted' style={{fontSize:"50%"}}> Order Number</div>
                                    <div style={{fontSize:"80%",fontWeight:"bold"}}> {customerOrder.customerOrderId}</div>
                                </div>
                            </div>
                            <div className='col-md-2'>
                                <div>
                                    <div className='text-muted' style={{fontSize:"50%"}}>Date</div>
                                    <div style={{fontSize:"80%",fontWeight:"bold"}}> {customerOrder.date}</div>
                                </div>
                            </div>
                            <div className='col-md-2'>
                                {customerOrder.status=='Ordered'?<div>
                                    <div>
                                        <div className='text-muted' style={{fontSize:"50%"}}>Customer Name</div>
                                        <div style={{fontSize:"80%",fontWeight:"bold"}}> {customerOrder.customerName} ({customerOrder.customerPhone})</div>
                                    </div>
                                </div>:null}
                            </div>
                            <div className='col-md-2'>
                                
                            </div>
                            <div className='col-md-2'>
                            
                                {customerOrder.status=='Ordered'?<div>
                                    <div>
                                        <div className='text-muted' style={{fontSize:"50%"}}>Sales Person</div>
                                        <div style={{fontSize:"80%",fontWeight:"bold"}}> {customerOrder.user.name} ({customerOrder.user.phone}) </div>
                                    </div>
                                </div>:null}
                            </div>
                            
                            <div className='col-md-2 h5'>
                                    {customerOrder.status}
                            </div>
                            </div>
                        </div>
                        <div class="card-body">
                            <table className='table table-bordered'>
                                <tr>
                                    <td>Product Picture</td>
                                    <td>Product Name</td>
                                    <td>Product Category</td>
                                    <td>Price</td>
                                    <td>Quantity</td>
                                    <td>Total <label style={{fontSize:"0"}}></label></td>
                                    {role=='Sales_Person' && customerOrder.status=='Added To Cart'?
                                    <td>Remove</td>
                                    :<td></td>}
                                    {role=='CustomerRepresentative' && customerOrder.status=='Ordered'?
                                    <td>Return Item</td>
                                    :<td></td>}
                                </tr>
                                {customerOrder.customerOrderedProductList.map((customerOrderedProduct, index) =>
                                <tr key={customerOrderedProduct.customerOrderedProductId}>
                                        <td>
                                            <img src={'data:image/png;base64,'+customerOrderedProduct.product.pictureName} style={{maxWidth:"100%",maxHeight:"50px"}}/>
                                        </td>
                                        <td>{customerOrderedProduct.product.productName}</td>
                                        <td>{customerOrderedProduct.product.categoryName} <span>=></span> {customerOrderedProduct.product.subCategoryName}</td>
                                        <td>$ {customerOrderedProduct.product.price}</td>
                                        <td>{customerOrderedProduct.quantity}</td>
                                        <td>$ {parseFloat(customerOrderedProduct.product.price)*parseFloat(customerOrderedProduct.quantity)} <span style={{fontSize:"0"}}>{ total  =total+ parseFloat(customerOrderedProduct.product.price)*parseFloat(customerOrderedProduct.quantity)}</span></td>
                                        {role=='Sales_Person' && customerOrder.status=='Added To Cart'?<td>
                                            <form onSubmit={submitHandlerRemoveFromCart}>
                                                <input type="hidden" name='customerOrderedProductId' value={customerOrderedProduct.customerOrderedProductId}/>
                                                <input type="hidden" name='customerOrderId' value={customerOrder.customerOrderId}/>
                                                <input type="submit" value="Remove" className='btn btn-danger'/>
                                            </form>
                                        </td>:<td></td>}
                                </tr>)}
                                <tr>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td>Total</td>
                                    <td><b>$ {total}</b></td>
                                    <td></td>
                                    
                                    {role=='Sales_Person' && customerOrder.status=='Added To Cart'?
                                    <td></td>
                                    :null}
                                    {role=='CustomerRepresentative' && customerOrder.status=='Ordered'?
                                    <td></td>
                                    :null}
                                </tr>
                            </table>
                        </div>
                        <div className="card-footer">
                        {role=='Sales_Person'?<div >
                        {customerOrder.status=='Added To Cart'?<div>
                            <form onSubmit={submitHandleSetStatus}>
                                <input type="hidden" name='customerOrderId' value={customerOrder.customerOrderId}/>
                                <input type="hidden" name='status' value='Ordered'/>
                                <div className='row'>
                                    <div className='col-md-3'></div>
                                    <div className='col-md-3'>
                                        <div className="mb-3 mt-3">
                                            <label for="customerName" className="form-label">Customer Name</label>
                                            <input type='text'  {...bindCustomerName}  className="form-control" placeholder="Enter Customer Name" required/>
                                        </div>
                                    </div>
                                    <div className='col-md-3'>
                                        <div className="mb-3 mt-3">
                                            <label for="customerPhone" className="form-label">Customer Phone Number</label>
                                            <input type='text'  {...bindCustomerPhone}  className="form-control" placeholder="Enter Customer Phone" required/>
                                        </div>
                                    </div>
                                    <div className='col-md-3 pt-4'>
                                        <input type="submit" value="Order Now" className='btn  mt-4 w-100 btn-primary'/>
                                    </div>
                                </div>
                                
                            </form>
                         </div>:<div></div>}
                        </div>:<div></div>}
                        </div>
                    </div>
                </div>
            )}
            </div>
        </div>)
}
export default AddToCart;