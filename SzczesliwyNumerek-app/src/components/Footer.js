import React from 'react'

const Footer = () => {
    return (
        <footer class="page-footer">
            <div class="container">
                <div class="row">
                    <div class="col l6 s12">
                            <h5 class="white-text">Szczęśliwy Numerek</h5>
                            <p class="grey-text text-lighten-4">Posiadanie szczęśliwego numerka gwarantuje, że nie zostaniesz zapytany!</p>
                    </div>
                    <div class="col l4 offset-l2 s12">
                        <h5 class="white-text">Zobacz też:</h5>
                        <ul>
                            <li><a class="grey-text text-lighten-3" href="http://www.zst-ostrow.edu.pl/">Zespół Szkół Technicznych</a></li>
                            <li><a class="grey-text text-lighten-3" href="https://iuczniowie.progman.pl/idziennik/login.aspx">iDziennik</a></li>
                            <li><a class="grey-text text-lighten-3" href="https://github.com/remes2000/SzczesliwyNumerek-app">GitHub - Klient Web</a></li>
                            <li><a class="grey-text text-lighten-3" href="https://github.com/remes2000/SzczesliwyNumerek-api">GitHub - Api</a></li>
                        </ul>
                    </div>
                </div>
            </div>
                <div class="footer-copyright">
                    <div class="container">
                        © {new Date().getFullYear()} Copyright Text
                    </div>
                </div>
        </footer>
    )
}

export default Footer