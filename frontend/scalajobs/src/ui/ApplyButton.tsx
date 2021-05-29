import React from 'react'

interface Props {
    href?: string, 
    width?: string
}

const Btn = ({href}: Props) => 
  <a
   children="Apply"
   href={href}
   className="applyButton"  
  />

export default Btn   
