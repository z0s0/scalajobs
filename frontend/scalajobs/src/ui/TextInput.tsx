import React from 'react'
import Tooltip from './Tooltip'

interface Props {
    onChange: (evt: React.ChangeEvent<HTMLInputElement>) => void,
    placeholder?: string, 
    tooltipText?: string,
    label?: string
}

const Input = ({tooltipText, placeholder, onChange, label}: Props): React.FunctionComponentElement<Props> =>
  <div>
    {tooltipText && <Tooltip text={tooltipText}/>}
    
    {label && <label children={label}/>}

    <input
      className="input"
      type="text"
      onChange={onChange}
      placeholder={placeholder}
    />
  </div>

export default Input  
  
