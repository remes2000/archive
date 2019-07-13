const mongoose = require('mongoose')
const { Schema } = mongoose
const DayDataSchema = require('./DayData')

const weeklyScheduleSchema = new Schema({
    weeklySchedule: { type: [DayDataSchema], required: true },
    createdDate: { type: Date, default: Date.now}
})

mongoose.model('weeklySchedules', weeklyScheduleSchema)