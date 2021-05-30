import React from 'react'

interface Props {
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void,
    placeholder?: string,
    tooltipText?: string,
    label?: string
}

export default ({onChange, label, placeholder}: Props): React.FunctionComponentElement<Props> => 
  <div>
    {label && <label children={label}/>}

    <input
      onChange={onChange}
      placeholder={placeholder}
      type="number"
      className="input"
    />
  </div>
  
