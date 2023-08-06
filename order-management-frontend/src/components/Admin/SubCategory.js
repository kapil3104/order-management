import React, { useEffect, useRef, useState } from 'react'
import useInput from '../../hooks/useInput'
import axios from 'axios'
import Cookies from 'js-cookie'
const rest = require('../../BackEndApis')

function SubCategories() {
    const [categoryId, setCategoryId,  bindCategoryId, resetCategoryId] = useInput('')
    const [subCategoryName, setSubCategoryName,  bindSubCategoryName, resetSubCategoryName] = useInput('')
    const [categories, setCategories] = useState([])
    const [subCategories, setSubCategories] = useState([])
    const [msgColor, setMsgColor] = useState('');
    const [count, setCount] = useState(0);
    const spinnerRef = useRef()
    const errorRef = useRef()
    const header = {
        headers: {
            "Content-type": "Application/json",
            "Authorization": `Bearer ${Cookies.get('token')}`
        }
    }
    useEffect(() => {
        axios.get(rest.endPointCategory+"all", header)
            .then(response => {
                console.log(response.data.data);
                setCategories(response.data.data)
                axios.get(rest.endPointSubCategory+"/all", header)
                .then(response => {
                    console.log(response.data.data);
                    setSubCategories(response.data.data)
                })
                .catch(err => {
                    console.log(err)
                })
            })
            .catch(err => {
                console.log(err)
            })
    }, [count]);
    const getSubCategories = (e)=>{

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
        if(subCategoryName==='' ) {
            errorRef.current.innerHTML = "<span>* Please Fill All Fields</span>"
        }  else {
            let data = {"subCategoryName":subCategoryName,categoryId:categoryId}
            console.log(data)
            axios.post(rest.endPointSubCategory+"/",{"subCategoryName":subCategoryName,categoryId:categoryId},header)
            .then(response => {
                if(response.status===200){
                    errorRef.current.innerHTML = "<span>* Sub Category Added Successfully </span>"
                    setMsgColor('text-success m-5 text-end')
                }else{
                    errorRef.current.innerHTML = "<span>* Failed to Add Sub Category </span>"
                    setMsgColor('text-danger mt-5 text-end')
                }
                setSubCategoryName("")
                setCategoryId("")
                setCount(count + 1)
            })
            .catch((err) => {
                console.log(err);
                alert("Fails to Added Successlly")
                errorRef.current.innerHTML = "<span>* Fails to Add SubCategory</span>"
            })
        }
    }
    return (
        <div className="container">
            <div className="text-center text-white h4">Sub Categories</div>
            <div className="row">
                <div className='col-md-6'>
                    <div className="card m-3 p-3">
                        <div className='text-center mytitle2'>Add Sub Category</div>
                    <form onSubmit={submitHandler}>
                        <div class="mb-3 mt-3">
                            <label for="categoryName" class="form-label">Category</label>
                            
                            <select className='form-control' {...bindCategoryId}>
                            <option value="">Choose Category</option>
                            {categories.map((category, index) =>
                                <option className="list-group-item" key={category.categoryId} value={category.categoryId}>
                                {category.categoryName}
                            </option>)}
                            </select>
                        </div>
                        <div class="mb-3 mt-3">
                            <label for="subCategoryName" class="form-label">Sub Category</label>
                            <input type='text'  {...bindSubCategoryName}  class="form-control" placeholder="Enter Sub Category Name"/>
                        </div>
                        <div className="d-grid">
                            <button type="submit"  ref={spinnerRef} className="btn btn-primary" >Add Sub Category</button>
                        </div>
                    </form>
                    <div>
                        <div ref={errorRef} className={msgColor}></div>
                    </div>
                    </div>
                    
                </div>
                <div className='col-md-6'>
                    <div class="mb-3 mt-3">
                        <div className="card m-3 p-3">
                            <div className='text-center mytitle2'>Sub Categories</div>
                            <div className="ps-4">
                                <label for="categoryName" class="form-label">Category</label>
                                <select className='form-control'  onChange={getSubCategories}>
                                    <option value="All">Choose Category</option>
                                    {categories.map((category, index) =>
                                        <option className="list-group-item" key={category.categoryId} value={category.categoryId}>
                                        {category.categoryName}
                                    </option>)}
                                </select>
                            </div>
                            <div className='mt-3'>
                                <ul>
                                    {subCategories.map((subCategory, index) =>
                                        <option  key={subCategory.subCategoryId} value={subCategory.subCategoryId}>
                                        {subCategory.subCategoryName}
                                    </option>)}
                                </ul>
                            </div>
                        </div>
                        </div>
                </div>
            </div>
        </div>
    )
}

export default SubCategories