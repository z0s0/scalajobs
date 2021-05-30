import '../styles/globals.css'
import '../styles/styles.css'
import Link from 'next/link'
import Head from 'next/head'

import ToggleSwitch from '../src/ui/ToggleSwitch'

function MyApp({ Component, pageProps }) {
  return (
    <>
       <Head>
        <title>Scalajobs</title>
       </Head>

       <header>
        <div className="content">
          <div>
            ScalaJobs 
          </div>

          <ToggleSwitch
            isChecked={true}
          />
          
          <Link href="/post_job">
            Post job
          </Link>
        </div>
      </header>

      <div className="pageContainer">
        <Component {...pageProps} />
      </div>
      
      <footer>
        <span>ScalaJobs.ru 2021</span>

        <a href="https://github.com/z0s0/scalajobs">contribute</a>

      </footer>
    </>
  )
}

export default MyApp
