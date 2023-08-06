import React, { useEffect, useRef, useState } from 'react'
import useInput from '../../hooks/useInput'
import axios from 'axios'
import Cookies from 'js-cookie'
const rest = require('../../BackEndApis')

function Category() {
    const [categoryName, setCategoryName,  bindCategoryName, resetCategoryName] = useInput('')
    const [categories, setCategories] = useState([])
    const [count, setCount] = useState(0);
    const [msgColor, setMsgColor] = useState('');
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
            })
            .catch(err => {
                console.log(err)
            })
    }, [count]);
    const submitHandler = (e) => {
        e.preventDefault()
        if(categoryName==='') {
            errorRef.current.innerHTML = "<span>* Please Fill All Fields</span>"
        }  else {
            axios.post(rest.endPointCategory,{"categoryName":categoryName},header)
            .then(response => {
                if(response.status===200){
                    errorRef.current.innerHTML = "<span>* Category Added Successfully</span>"
                    setMsgColor('text-success m-5 text-end')
                }else{
                    errorRef.current.innerHTML = "<span>* Failed to Add Category</span>"
                    setMsgColor('text-danger mt-5 text-end')
                }
                setCategoryName("")
                setCount(count + 1)
            })
            .catch((err) => {
                console.log(err.response.data.error);
                errorRef.current.innerHTML = "<span>* Failed to Add Catrgory</span>"
            })
        }
    }
    return (
        <div className="container">
            <div className="row">
                <div className="col-md-6">
                    <div className="card p-4 m-5">
                            <div className='text-center mytitle2'>Add Category</div>
                            <form onSubmit={submitHandler}>
                                <div class="mb-3 mt-3">
                                    <label for="categoryName" class="form-label">Category</label>
                                    <input type='text'  {...bindCategoryName}  class="form-control" placeholder="Enter Category Name"/>
                                </div>
                                <div className="d-grid">
                                    <button type="submit" value="Login" ref={spinnerRef} className="btn btn-primary" >Add Category</button>
                                </div>
                            </form>
                            <div>
                                <div ref={errorRef} className={msgColor}></div>
                            </div>
                    </div>
                    
                </div>
                <div className="col-md-6">
                    <div className='card p-4 m-5'>
                    <div className='text-center mytitle2'>Category List</div>
                        <ul class="list-group mt-5">
                            {categories.map((category, index) =>
                                        <li className="list-group-item" key={category._id}>
                                        {category.categoryName}
                                        </li>)}
                        </ul>
                    </div>
                
                </div>
            </div>
        </div>
    )
}

export default Category
