import React from 'react'

interface Props {
    text: string,
    onClick: () => void
}

export default ({text, onClick}: Props) =>
  <button
    onClick={onClick}
    children={text}
  />
