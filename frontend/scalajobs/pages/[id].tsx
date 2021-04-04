import React from 'react'
import {getVacancy, listVacancies} from "../src/api"

export default ({job}) => {
  return(
    <div>
      {job.id}, {job.description}
    </div>
  )
}

export async function getStaticProps({params}) {
  const res = await getVacancy(params.id)
  const job = res.data
  return {props: {job}}
}

export async function getStaticPaths() {
  const jobs = await listVacancies()

  return {
    paths: jobs.data?.map(({ id }) => `/${id}`) ?? [],
    fallback: false,
  }
}
