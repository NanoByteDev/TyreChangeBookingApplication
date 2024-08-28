<template>
  <div id="app">
    <h1>Tyre Change Booking</h1>
    <div v-if="error" class="error">{{ error }}</div>
    
    <div>
      <h2>Select a Workshop</h2>
      <select v-model="selectedWorkshop" @change="handleWorkshopSelect">
        <option value="" disabled selected>Select a workshop</option>
        <option v-for="workshop in parsedWorkshops" :key="workshop.name" :value="workshop">
          {{ workshop.name }} - {{ workshop.address }}
        </option>
      </select>
    </div>

    <div v-if="selectedWorkshop">
      <h2>Select Dates and Vehicle Type</h2>
      <div>
        <label for="fromDate">From:</label>
        <input type="date" id="fromDate" v-model="fromDate">
      </div>
      <div>
        <label for="untilDate">Until:</label>
        <input type="date" id="untilDate" v-model="untilDate">
      </div>
      <div>
        <label for="vehicleType">Vehicle Type:</label>
        <select id="vehicleType" v-model="selectedVehicleType">
          <option v-for="type in selectedWorkshop.vehicleTypes" :key="type" :value="type">
            {{ type }}
          </option>
        </select>
      </div>
      <button @click="fetchAvailableTimes" :disabled="!canFetchTimes">Fetch Available Times</button>
    </div>

    <div v-if="availableTimes.length > 0">
      <h2>Select a Time</h2>
      <select v-model="selectedTime" @change="handleTimeSelection">
        <option value="" disabled selected>Select a time</option>
        <option v-for="time in availableTimes" :key="time.id" :value="time">
          {{ formatDateTime(time.time) }}
        </option>
      </select>
    </div>

    <div v-if="selectedTime">
      <h2>Selected Time</h2>
      <p>{{ formatDateTime(selectedTime.time) }}</p>
      <div>
        <label for="contactInfo">Contact Information:</label>
        <input type="text" id="contactInfo" v-model="contactInformation" required>
      </div>
      <button @click="bookAppointment" :disabled="!canBook">Book Appointment</button>
    </div>

    <div v-if="bookingConfirmation">
      <h2>Booking Confirmation</h2>
      <p>Your booking has been confirmed!</p>
      <p><strong>Booking ID:</strong> {{ bookingConfirmation.id }}</p>
      <p><strong>Workshop:</strong> {{ bookingConfirmation.workshopName }}</p>
      <p><strong>Date and Time:</strong> {{ bookingConfirmation.formattedBookingTime }}</p>
      <p><strong>Vehicle Type:</strong> {{ bookingConfirmation.vehicleType }}</p>
      <p><strong>Contact Information:</strong> {{ bookingConfirmation.contactInformation }}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'App',
  data() {
    return {
      parsedWorkshops: [],
      selectedWorkshop: null,
      fromDate: '',
      untilDate: '',
      selectedVehicleType: '',
      availableTimes: [],
      selectedTime: null,
      contactInformation: '',
      bookingConfirmation: null,
      error: null
    };
  },
  computed: {
    canFetchTimes() {
      return this.selectedWorkshop && this.fromDate && this.untilDate && this.selectedVehicleType;
    },
    canBook() {
      return this.selectedTime && this.contactInformation.trim() !== '';
    }
  },
  mounted() {
    this.fetchWorkshops();
  },
  methods: {
    async fetchWorkshops() {
      try {
        const response = await axios.get('http://localhost:8080/api/workshops');
        this.parsedWorkshops = response.data;
      } catch (error) {
        console.error('Error fetching workshops:', error);
        this.error = 'Failed to fetch workshops. Please try again later.';
      }
    },
    handleWorkshopSelect() {
      this.availableTimes = [];
      this.selectedTime = null;
      this.fromDate = '';
      this.untilDate = '';
      this.selectedVehicleType = this.selectedWorkshop.vehicleTypes[0];
    },
    async fetchAvailableTimes() {
      if (!this.canFetchTimes) return;
      try {
        const response = await axios.get(`http://localhost:8080/api/workshops/${this.selectedWorkshop.name}/available-times`, {
          params: {
            from: this.fromDate,
            until: this.untilDate,
            vehicleType: this.selectedVehicleType
          }
        });
        this.availableTimes = response.data;
      } catch (error) {
        console.error('Error fetching available times:', error);
        this.error = 'Failed to fetch available times. Please try again later.';
      }
    },
    handleTimeSelection() {
      console.log('Selected time:', this.selectedTime);
    },
    async bookAppointment() {
      if (!this.canBook) return;
      try {
        const response = await axios.post('http://localhost:8080/api/bookings', {
          workshopName: this.selectedWorkshop.name,
          timeId: this.selectedTime.id,
          vehicleType: this.selectedVehicleType,
          contactInformation: this.contactInformation
        });
        this.bookingConfirmation = response.data;
      } catch (error) {
        console.error('Error booking appointment:', error);
        this.error = 'Failed to book appointment. Please try again later.';
      }
    },
    formatDateTime(dateTime) {
      return new Date(dateTime).toLocaleString();
    }
  }
};
</script>

<style scoped>
#app {
  font-family: Arial, sans-serif;
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

h1, h2 {
  color: #333;
}

select, input, button {
  margin: 10px 0;
  padding: 5px;
  font-size: 16px;
}

button {
  background-color: #4CAF50;
  color: white;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.error {
  color: red;
  margin-bottom: 10px;
}
</style>