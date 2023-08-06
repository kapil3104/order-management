import React, { useEffect, useRef, useState } from 'react'
import axios from 'axios'
import useInput from '../../hooks/useInput'
import { useHistory } from "react-router-dom";
import Cookies from 'js-cookie'
const rest = require('../../BackEndApis')
function ViewProducts() {
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
    const header = {
        headers: {
            "Content-type": "Application/json",
            "Authorization": `Bearer ${Cookies.get('token')}`
        }
    }
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
        let quantity = e.target[1].value;
        let available = e.target[2].value;
        if(parseInt(available)<parseInt(quantity)){
            alert("Only "+available+" Products Available")
            e.target[1].value = ''+available
        }else {
            axios.get(rest.endPointProductAddToCart+"?productId="+productId+"&quantity="+quantity,header)
            .then(response => {
               alert(response.data)
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
            <div className="row" >
                    {products.map((product, index) =>
                    <div className="col-md-4 mt-3" key={product.productId}>
                       <div class="card">
                            <div class="card-header h6  text-uppercase">
                                <div className='row'>
                                    <div className='col-md-6'>
                                        {product.productName}
                                    </div>
                                    <div className='col-md-6 text-end'>
                                        {product.productCode}
                                    </div>
                                </div>
                                
                            </div>
                            <div class="card-body">
                            <div className='row'>   
                                <div className='col-md-6'>
                                    <img src={'data:image/png;base64,'+product.pictureName} style={{maxWidth:"100%",maxHeight:"200px"}}/>
                                </div>
                                <div className='col-md-6' style={{height:'200px'}}>
                                    <div className="row">
                                        <div className='col-md-12'>
                                            <div className="text-muted" style={{fontSize:"70%"}}>Price</div>
                                            <div className="h6">$ {product.price}</div>
                                        </div>
                                        <div className='col-md-12'>
                                            <div className="text-muted" style={{fontSize:"70%"}}>Available</div>
                                            <div className="h6">{product.availableQuantity}</div>
                                        </div>
                                    </div>
                                    <div className="row">
                                        <div className='col-md-12'>
                                            <div className="text-muted" style={{fontSize:"70%"}}>Category</div>
                                            <div className="h6">{product.categoryName}</div>
                                        </div>
                                        <div className='col-md-12'>
                                            <div className="text-muted" style={{fontSize:"70%"}}>Sub Category</div>
                                            <div className="h6">{product.subCategoryName}</div>
                                        </div>
                                    </div>
                                   
                                    </div>
                                </div>
                                <div style={{fontSize:"80%"}} className="ps-3 pe-3">{product.description}</div>
                            </div>

                            <div class="card-footer">
                                {role=='Stock_Manager'?
                                    <div>
                                        <form onSubmit={submitHandlerUpdateQuantity}>
                                            <div className='row'>
                                                <div className='col-md-8'>
                                                    <input type="hidden" name="productId" value={product.productId}/>
                                                <div className="mb-3 mt-3">
                                                    <label  className="form-label">Add or Remove Quantity</label>
                                                    <input type='number'  className="form-control" placeholder="Enter Quantity"/>
                                                </div>
                                                </div>
                                                <div className='col-md-4 mt-5'>
                                                    <button className='btn btn-primary w-100'>Update</button>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                  : <div>{role=='Sales_Person'?
                                    <div>
                                     <form onSubmit={submitHandlerCardToCart}>
                                            <div className='row'>
                                                <div className='col-md-8'>
                                                    <input type="hidden" name="productId" value={product.productId}/>
                                                <div className="mb-3 mt-3">
                                                    <label  className="form-label">Add To Cart</label>
                                                    <input type='number'  className="form-control" placeholder="Enter Quantity"/>
                                                </div>
                                                </div>
                                                <input type="hidden" name="availableQuantity" value={product.availableQuantity}/>
                                                <div className='col-md-4 mt-5'>
                                                    <button className='btn btn-primary w-100'>Add</button>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                    : null}
                                    </div>
                                 }
                            </div>
                        </div>
                    </div>)}
                </div>   
        </div>
        </div>)
}
export default ViewProducts;