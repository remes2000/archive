import React from 'react'

const Header = () => {
    return (
        <nav>
            <div className="nav-wrapper">
                <a href="/" className="brand-logo">
                    Szczęśliwy Numerek
                </a>
                <ul className="right hide-on-med-and-down">
                    <li><a href="https://szczesliwy-numerek-api.herokuapp.com/">API</a></li>
                </ul>
            </div>
        </nav>
    )
}

export default Header