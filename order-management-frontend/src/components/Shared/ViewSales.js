import React, { useEffect, useRef, useState } from 'react'
import axios from 'axios'
import useInput from '../../hooks/useInput'
import { useHistory } from "react-router-dom";
import Cookies from 'js-cookie'
import { useLocation } from 'react-router-dom';
const rest = require('../../BackEndApis')
function ViewSales(props) {
    const history = useHistory();
    const location = useLocation();
    let search = window.location.search;
    let params = new URLSearchParams(search);
    let type = params.get('type');
    let total = 0;

    let type2 = params.get('type');
    const [customerOrders, setCustomerOrders] = useState([])
    const [count, setCount] = useState(0);
    const [role, setRole] = useState('')
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
        setCount(count+1)
      }, [location]);
    useEffect(() => {
        setRole(Cookies.get('role'))
        axios.get(rest.endPointProductOrders+"?type="+type+"&customerOrderId="+customerOrderId,header)
            .then(response => {
                console.log(response.data.data);
                setCustomerOrders(response.data.data)
            })
            .catch(err => {
                console.log(err)
            })
    }, [count]);


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
    const submitHandlerReturnItem= (e) => {
        e.preventDefault()
        console.log(e);
        let orderedProductId = e.target[0].value;
        let customerOrderId= e.target[1].value;
        let quantityToReturn= e.target[2].value;
            axios.get(rest.endPointProductReturnItem+"?orderedProductId="+orderedProductId+"&customerOrderId="+customerOrderId+"&quantity="+quantityToReturn,header)
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

    const handleExportPDF = async () => {
        try {
            const exportHeader = {
                headers: {
                    "Content-type": "Application/json",
                    "Authorization": `Bearer ${Cookies.get('token')}`
                },
                responseType: 'blob'
            }
          const response = await axios.get(rest.endPointExportPDF, exportHeader);
          const blob = new Blob([response.data], { type: 'application/pdf' });
          const url = URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = 'generatedPDF.pdf';
          link.click();
          URL.revokeObjectURL(url);
        } catch (error) {
          console.error('Error generating PDF:', error.message);
        }
    };

    return (
        <div style={{backgroundColor:'#2c3e50'}}>
        <div className="container" >
            {type=='ordered'?
            <div className='row'>
                <div className='col-md-3'>
                    <div className='mt-2'>
                        <label className='form-control-lable text-white'>Order Number</label>
                        <tr>
                            <td><input type='number' {...bindCustomerOrderId}  className='form-control' placeholder='Search with Order Number' onKeyUp={()=>{setCount(count+1)}} required/></td>
                            <td><input type="submit" value="Export PDF" className='btn  mt-4 w-100 btn-primary' onClick={handleExportPDF}/></td>
                        </tr>
                    </div>
                </div>
            </div>
            : null}           
            {customerOrders.map((customerOrder, index) =>
                <div key={customerOrder.customerOrderId}>
                    <span style={{display:'None'}}>{total =0 }</span>
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
                                    {role=='Customer_Representative' && customerOrder.status=='Ordered'?
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
                                        <td> $ {parseFloat(customerOrderedProduct.product.price)*parseFloat(customerOrderedProduct.quantity)} 
                                            <span style={{fontSize:"0"}}>
                                            {customerOrderedProduct.status!='Returned'?<span>
                                                {total  =total+ parseFloat(customerOrderedProduct.product.price)*parseFloat(customerOrderedProduct.quantity)}
                                                </span>
                                           :<span>
                                            </span>}
                                            </span>
                                        </td>
                                        {role=='Sales_Person' && customerOrder.status=='Added To Cart'?<td>
                                            <form onSubmit={submitHandlerRemoveFromCart}>
                                                <input type="hidden" name='customerOrderedProductId' value={customerOrderedProduct.customerOrderedProductId}/>
                                                <input type="hidden" name='customerOrderId' value={customerOrder.customerOrderId}/>
                                                <input type="submit" value="Remove" className='btn btn-danger'/>
                                            </form>
                                        </td>:<td></td>}
                                        {role=='Customer_Representative' && customerOrder.status=='Ordered' && customerOrderedProduct.status!='Returned'?
                                        <td>
                                            <form onSubmit={submitHandlerReturnItem}>
                                                <input type="hidden" name='customerOrderedProductId' value={customerOrderedProduct.customerOrderedProductId}/>
                                                <input type="hidden" name='customerOrderId' value={customerOrder.customerOrderId}/>
                                                <input type="number" min="1" max={customerOrderedProduct.quantity} name='quantity' placeholder="Enter Quantity To Return" required />
                                                <input type="submit" value="Take Return" className='btn btn-info'/>
                                            </form>
                                        </td>:
                                        <td>
                                            {customerOrderedProduct.status ==='Returned'?<div className='text-danger'>
                                                Returned
                                            </div>:<div></div>}       
                                            </td>}
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
                                    {role=='Customer_Representative' && customerOrder.status=='Ordered'?
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
                </div>)}
        </div></div>
    )
}

export default ViewSales