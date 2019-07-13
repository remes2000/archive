const mongoose = require('mongoose')
const { Schema } = mongoose

const dayDataSchema = new Schema({
    number: Number,
    date: Date
})

module.exports = dayDataSchema