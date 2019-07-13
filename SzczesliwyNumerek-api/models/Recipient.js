const mongoose = require('mongoose')
const { Schema } = mongoose

const recipientSchema = new Schema({
    number: { type: Number, required: true },
    email: { type: String, required: true }
})

mongoose.model('recipients', recipientSchema)