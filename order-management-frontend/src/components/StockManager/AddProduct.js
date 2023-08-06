import React, { useEffect, useRef, useState } from 'react'
import axios from 'axios'
import useInput from '../../hooks/useInput'
import { useHistory } from "react-router-dom";
import Cookies from 'js-cookie'
//import Alert from 'react-popup-alert'
const rest = require('../../BackEndApis')



function AddProduct() {

  
  /*  const [Alert, setAlert] = React.useState({
        type: 'error',
        text: 'This is a alert message',
        show: false
    })

    function onShowAlert(textmessage) {
        setAlert({
        type: 'success',
        text: textmessage,
        show: true
        })
    }*/

    const history = useHistory();
    const [productName, setProductName,  bindProductName, resetProductName] = useInput('')
    const [productCode, setProductCode,  bindProductCode, resetProductCode] = useInput('')
    const [price, setPrice, bindPrice, resetPrice] = useInput('')
    const [availableQuantity, setAvailableQuantity,  bindAvailableQuantity, resetAvailableQuantity] = useInput('')
    const [description, setDescription, bindDescription, resetDescription] = useInput('')
    const [pictureName, setPictureName, bindPictureName, resetPictureName] = useInput('')
  
    const [categoryId, setCategoryId,  bindCategoryId, resetCategoryId] = useInput('')
    const [subCategoryId, setSubCategoryId,  bindSubCategoryId, resetSubCategoryId] = useInput('')
    const [categories, setCategories] = useState([])
    const [subCategories, setSubCategories] = useState([])
    const [state, setState]  = useState();

    const [count, setCount]  = useState(0);
    const spinnerRef = useRef()
    const errorRef = useRef()
    const header = {
        headers: {
            "Content-type": "Application/json",
            "Authorization": `Bearer ${Cookies.get('token')}`
        }
    }
    const header_form_data = {
        headers: {
            "Content-type": "multipart/form-data",
            "Authorization": `Bearer ${Cookies.get('token')}`
        }
    }
    const fileSelectedHandler = (event) => {
        setState({
            selectedFile: event.target.files[0],
            filename: event.target.files
        })
    }

    useEffect(() => {
        axios.get(rest.endPointCategory+"all",header)
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
    }
    const submitHandler = (e) => {
        e.preventDefault()
        if(productName==='' || price===''|| availableQuantity==='' || description==='' ||  categoryId==='' || subCategoryId==='') {
            errorRef.current.innerHTML = "<span>* Please Fill All Fields</span>"
        }  else {
            const postData = {
                productName: productName,
                productCode: productCode,
                price: price,
                availableQuantity: availableQuantity,
                description: description,
                subCategoryId: subCategoryId,
                categoryId: categoryId
              };
            let formData = new FormData();
            const jsonString = JSON.stringify(postData);
            formData.append('productDto', new Blob([jsonString], { type: 'application/json' }));
            formData.append('file', state.selectedFile);
            axios.post(rest.endPointProduct, formData, header_form_data).then(response => {
                console.log(response)
                if(response.status === 200){
                    setProductName('')
                    setPrice('')
                    setAvailableQuantity('')
                    setDescription('')
                    setProductCode('')
                    setCategoryId('')
                    setSubCategoryId('')
                    document.getElementById("categoryId").value = ''
                    document.getElementById("pictureName").value = ''
                    alert('Product has been added successfully.')
                }
            }).catch(err => {
                console.log(err)
                alert('Error While Adding The Product')
            })
        }
    }
    return (
        <div>
            <div className="container">
                    <div className="card p-4 m-5">
                        <div className="text-center h4">Add New Product</div>
                        <form onSubmit={submitHandler}>
                            <div className='row'>
                                <div className='col-md-6'>
                                <div class="mb-3 mt-3">
                                    <label for="categoryName" class="form-label">Category</label>
                                        <select className='form-control'  id='categoryId' onChange={getSubCategories}>
                                        <option value=''>Choose Category</option>
                                        {categories.map((category, index) =>
                                            <option className="list-group-item" key={category.categoryId} value={category.categoryId}>
                                            {category.categoryName}
                                        </option>)}
                                        </select>
                                    </div>
                                    <div className="mb-3 mt-3">
                                        <label for="producCode" className="form-label">Product Code</label>
                                        <input type='text'  {...bindProductCode}  className="form-control" placeholder="Enter Product Code"/>
                                    </div>

                                    <div className="mb-3 mt-3">
                                        <label for="availableQuantity" className="form-label">Available Quantity</label>
                                        <input type='number'  {...bindAvailableQuantity}  className="form-control" placeholder="Enter Available Quantity"/>
                                    </div>
                                    <div className="mb-3 mt-3">
                                        <label for="description" className="form-label">Description</label>
                                        <textarea placeholder='Enter description'   {...bindDescription}  className="form-control"></textarea>
                                    </div>
                                </div>
                                <div className='col-md-6'>
                                    <div className="mt-3">
                                        <label for="s" class="form-label">Sub Category</label>
                                            <select className='form-control' {...bindSubCategoryId}>
                                                <option value="All">Choose Sub Category</option>
                                                {subCategories.map((subCategory, index) =>
                                                    <option className="list-group-item" key={subCategory.subCategoryId} value={subCategory.subCategoryId}>
                                                    {subCategory.subCategoryName}
                                                </option>)}
                                            </select>
                                    </div>
                                    <div className="mb-3 mt-3">
                                        <label for="productName" className="form-label">Product Name</label>
                                        <input type='text'  {...bindProductName}  className="form-control" placeholder="Enter Product Name"/>
                                    </div>
                                    <div className="mb-3 mt-3">
                                        <label for="price" className="form-label">Price</label>
                                        <input type='number'  {...bindPrice}  className="form-control" placeholder="Enter Price"/>
                                    </div>
                                    <div className="mb-3 mt-3">
                                        <label for="pictureName" className="form-label">Product Pic</label>
                                        <input type='file' onChange={fileSelectedHandler} id="pictureName" className="form-control"/>
                                    </div>
                                </div>

                            </div>
                           
                            <div className="d-grid">
                                <button type="submit" ref={spinnerRef} className="btn btn-primary" >Add Product</button>
                            </div>
                            
                        </form>
                    </div>
                    <div >
                        <div ref={errorRef} className="text-danger m-5 text-end"></div>
                    </div>
                </div>
        </div>
    );
}


export default AddProduct
