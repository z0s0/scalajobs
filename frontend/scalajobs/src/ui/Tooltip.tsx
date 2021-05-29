import React from 'react'

interface Props {
    text: string 
}

export default ({text}: Props): React.FunctionComponentElement<Props> => 
  <div>
      {text}
  </div>
