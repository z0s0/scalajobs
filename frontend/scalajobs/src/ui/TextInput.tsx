import React from 'react'
import Tooltip from './Tooltip'

interface Props {
    onChange: () => void,
    placeholder?: string, 
    tooltipText?: string,
    label?: string
}

export default ({tooltipText, placeholder, onChange, label}: Props): React.FunctionComponentElement<Props> =>
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
  
