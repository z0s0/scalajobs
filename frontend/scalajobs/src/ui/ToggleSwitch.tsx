import React from 'react'

interface Props {
    onChange: () => void,
    isChecked: boolean
}

export default ({onChange, isChecked}: Props): React.FunctionComponentElement<Props> => 
  <div>
      {isChecked} toggle
  </div>
