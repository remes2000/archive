import React from 'react'
import { BrowserRouter, Route } from 'react-router-dom'
import '../customStyles.css'

import Header from './Header'
import Footer from './Footer'
import Dashboard from './Pages/Dashboard'

 const App = () => {
    return (
        <div>
            <BrowserRouter>
                <div>
                    <Header />
                    <div className="container">
                        <Route exact path="/" component={Dashboard} />
                    </div>
                    <Footer />
                </div>
            </BrowserRouter>
        </div>
    )
}

export default App