<template>
    <div>
      <h3>Select Workshop</h3>
      <select v-model="selectedWorkshop" @change="$emit('workshop-selected', selectedWorkshop)">
        <option v-for="workshop in workshops" :key="workshop.id" :value="workshop">
          {{ workshop.name }}
        </option>
      </select>
    </div>
  </template>
  
  <script>
  export default {
    name: 'WorkshopSelector',
    props: ['workshops'],
    data() {
      return {
        selectedWorkshop: null
      }
    }
  }
  </script>