import React from 'react'

interface Props {
    onChange: () => void,
    placeholder?: string,
    tooltipText?: string
}

export default ({onChange, placeholder}: Props): React.FunctionComponentElement<Props> => 
  <input
    onChange={onChange}
    placeholder={placeholder}
    type="number"
    className="input"
  />
