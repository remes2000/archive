const schedule = require('node-schedule')
const Randomizer = require('./Randomizer')
const mongoose = require('mongoose')
const Mailer = require('./Mailer')

const WeeklySchedule = mongoose.model('weeklySchedules')
const Recipient = mongoose.model('recipients')

schedule.scheduleJob({hour: 23, minute: 0, dayOfWeek: 5}, () => {

    const schedule = new WeeklySchedule({
        weeklySchedule: createNewWeeklySchedule()
    })

    schedule.save()

})

function createNewWeeklySchedule(){
    const today = new Date()
    const randomizer = new Randomizer()
    const numbers = randomizer.randomizeDifferentNumbers(1, 36, 5)
    return numbers.map( (number, index) => ({ number, date: getNext(index + 1, today) }) )
}

function getNext(dayOfWeek, date){
    const acctualDate = new Date(date.getTime())

    acctualDate.setDate(acctualDate.getDate() + 1)

    while(acctualDate.getDay() != dayOfWeek) {
        acctualDate.setDate(acctualDate.getDate() + 1)
    }

    return acctualDate
}

schedule.scheduleJob({hour: 6, minute: 0}, async () => {
    const schedule = await WeeklySchedule.findOne({}).sort( {_id: -1} )
    const today = new Date()
    const todayNumber = schedule.weeklySchedule[today.getDay() - 1]
    if(todayNumber){
        const recipientsList = await getRecipients(todayNumber)
        if(recipientsList)
            Mailer.sendNotification(recipientsList, todayNumber)
    }
})

async function getRecipients(number){
    const recipients = await Recipient.find({ number })
    const sendTo = recipients.reduce( (previousValue, currentValue) => {
        if(previousValue)
            return previousValue + ', ' + currentValue.email
        else
            return currentValue.email
    }, '')
    return sendTo
}