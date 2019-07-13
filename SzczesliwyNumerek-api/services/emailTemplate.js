module.exports = (number, date) => {
    return `
        <html>
            <head>
                <link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet">
            </head>
            <body style="font-family: 'Roboto', sans-serif;">
                <main style="border: .5rem solid #ee6e73; padding: 1.5rem;">
                    <h1>Dziś nie będziesz pytany!</h1>
                    <p>Szczęście dziś należy do Ciebie!</p>
                    <p>Wylosowaliśmy numer: ${number} który dzisiaj ( ${date} ) nie może zostać przepytany!</p>
                    <p>Pozdrawiam: <i>Szczęśliwy Numerek</i></p>
                </main>
            </body>
        </html>
    `
}