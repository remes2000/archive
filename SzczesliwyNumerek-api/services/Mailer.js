const nodemailer = require('nodemailer')
const template = require('./emailTemplate')
const keys = require('../config/keys')

const transport = nodemailer.createTransport({
    service: 'gmail',
    auth: {
      user: keys.emailAddress,
      pass: keys.emailPassword
    }
})

module.exports = {

    sendNotification: (recipients, todayNumber) => {
        transport.sendMail({
            from: '<szczesliwynumerek@noreply.com>',
            to: recipients,
            subject: 'Dziś nie będziesz pytany !',
            html: template(todayNumber.number, todayNumber.date.split('T')[0])
        }, (error, info ) => {
            if(error) return console.log(error)
            console.log('Message sent: ' + info.response)
        })
    }

}