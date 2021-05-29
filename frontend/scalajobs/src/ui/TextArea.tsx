import React from 'react'
import Tooltip from './Tooltip'

interface Props {
    onChange: (evt: React.ChangeEvent<HTMLTextAreaElement>) => void,
    placeholder: string,
    tooltipText?: string,
    label?: string
}

export default ({placeholder, onChange, tooltipText, label}: Props) => 
  <>
    {label && <label children={label}/>}

    {tooltipText && <Tooltip text={tooltipText}/>}

    <textarea 
      onChange={onChange}
      cols={30}
      rows={10}
      placeholder={placeholder}
      className="input"
    />
  </>
