const express = require('express')
const app = express()
const mongoose = require('mongoose')
const cors = require('cors')
const bodyParser = require('body-parser')

app.use(cors())

app.use(bodyParser.json())

require('./models/DayData')
require('./models/WeeklySchedule')
require('./models//Recipient')
const keys = require('./config/keys')

const WeeklySchedule = mongoose.model('weeklySchedules')
const Recipient = mongoose.model('recipients')

mongoose.connect(keys.mongoURI)

require('./services/schedule')
const Randomizer = require('./services/Randomizer')

app.get('/', (req, res) => {
    res.send(
        `
            <center>
                <h1>SzczesliwyNumerek-api</h1>
            </center>
            <h2>Documentation:</h2>
            <ul>
                <li>
                    <h3>/api/getWeeklySchedule</h3>
                    <p>Returns acctual lucky numbers with dates</p>
                </li>
                <li>
                    <h3>/api/addNewRecipient</h3>
                    <p>Method: Post</p>
                    <p>Request object should look like this:</p>
                    <p>{ number: 24, email: example@asd.com}</p>
                    <p>Returns { success: true } or status error number</p>
            </ul>
            <h2>Inofrmations</h2>
            <ul>
                <li>Application uses polish timezone UTC+01:00 / UTC+02:00
                <li>Application randoms numbers every Friday at 11 PM</li>
                <li>Application sends emails at 6 AM</li>
            </ul>
        `
    )
})

app.get('/api/getWeeklySchedule', async (req, res) => {
    const schedule = await WeeklySchedule.findOne({}).sort( {_id: -1} )
    res.json(schedule)
})

app.post('/api/addNewRecipient', async (req, res) => {
    const { number, email } = req.body
    const existingRecipient = await Recipient.findOne({number, email})
    if(!existingRecipient) {
        const recipient = await new Recipient({number, email}).save()
        res.json({success: true})
    } else res.status(409).send()
})

app.listen( process.env.PORT || 8080 )