import  { useState } from 'react'

function useInput(intialValue) {
    const [name,setName] = useState(intialValue)  // react 
    const reset = () =>{
        setName(intialValue)
    }
    const bind = {
        value: name,
        onChange : e => {setName(e.target.value)}
    }
    const setValue = (value) =>{
        setName(value)
    }
  return [name,setValue,bind, reset]
}

export default useInput

