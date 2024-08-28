<template>
    <div>
      <h3>Book Tyre Change</h3>
      <form @submit.prevent="submitBooking">
        <div>
          <label>Vehicle Type:</label>
          <input v-model="booking.vehicleType" required>
        </div>
        <div>
          <label>Date and Time:</label>
          <input type="datetime-local" v-model="booking.bookingTime" required>
        </div>
        <button type="submit">Book</button>
      </form>
    </div>
  </template>
  
  <script>
  export default {
    name: 'BookingForm',
    props: ['workshop'],
    data() {
      return {
        booking: {
          vehicleType: '',
          bookingTime: ''
        }
      }
    },
    methods: {
      submitBooking() {
        this.$emit('booking-submitted', { ...this.booking, workshop: this.workshop })
      }
    }
  }
  </script>