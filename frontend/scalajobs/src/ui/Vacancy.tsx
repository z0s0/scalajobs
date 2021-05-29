import React from 'react'
import Link from 'next/link'

import { Vacancy } from '../types'

interface Props {
    vacancy: Vacancy
}

interface BtnProps {
    href: string
}

export default ({vacancy}: Props) =>
   <div className="vacancy">

      id: {vacancy.id} 
      Vacancy {vacancy.description}

      <ReadMoreBtn
        href={`/${vacancy.id}`}
      />
      <ApplyBtn
        href={vacancy.link}
      />
  </div>


const ReadMoreBtn = ({href}: BtnProps) => 
  <Link
    href={href}
    children="Read more"
  />

const ApplyBtn = ({href}: BtnProps) => 
  <a
    children="Apply"
    href={href}
  />  
