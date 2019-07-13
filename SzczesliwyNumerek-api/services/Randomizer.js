class Randomizer{
    prepareArray(minNumber, maxNumber) {
        const array = []
        for(let i=minNumber; i<=maxNumber; i++){
            array.push(i)
        }
        return array
    }

    randomizeDifferentNumbers(minNumber, maxNumber, howMany) {
        const array = []
        const arrayOfPossibleNumbers = this.prepareArray(minNumber, maxNumber)
        let border = arrayOfPossibleNumbers.length - 1
        for(let i=0; i<howMany; i++){
            const randomizeResult = Math.floor( (Math.random() * border))
            array.push(arrayOfPossibleNumbers[randomizeResult])

            const element = arrayOfPossibleNumbers[randomizeResult]
            arrayOfPossibleNumbers[randomizeResult] = arrayOfPossibleNumbers[border]
            arrayOfPossibleNumbers[border] = element
            border --
        }
        return array
    }

}

module.exports = Randomizer