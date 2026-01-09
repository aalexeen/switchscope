<script setup>
//import router from '@/router';
import { reactive, onMounted } from 'vue';
import { useToast } from 'vue-toastification';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();

const userId = route.params.id;

const form = reactive({
    first_name: '',
    last_name: '',
    nickname: '',
    description: '',
    subdivision: 'BHMC',
    team: 'Support Group',
    room: '',
    contactEmail: '',
    contactPhone: '',
    registration: {
        date: '',
        last_login_date: ''
    }
});

const state = reactive({
    user: {},
    isLoading: true
});

const toast = useToast();

const hanbleSubmit = async () => {
    const updatedUser = {
        first_name: form.first_name,
        last_name: form.last_name,
        nickname: form.nickname,
        description: form.description,
        subdivision: form.subdivision,
        team: form.team,
        room: form.room,
        contactEmail: form.contactEmail,
        contactPhone: form.contactPhone,
        registration: {
            date: form.registration.date,
            last_login_date: form.registration.last_login_date
        }
    };

    try {
        const response = await axios.put(`/api/users/${userId}`, updatedUser);
        toast.success('User Updated Successfully');
        router.push(`/users/allusers/${response.data.id}`);
    } catch (error) {
        console.error('Error updating user', error);
        toast.error('User Was Not Updated');
    }
};

onMounted(async () => {
    try {
        const response = await axios.get(`/api/users/${userId}`);
        state.user = response.data;
        // Populate inputs
        form.first_name = state.user.first_name;
        form.last_name = state.user.last_name;
        form.nickname = state.user.nickname;
        form.description = state.user.description;
        form.subdivision = state.user.subdivision;
        form.team = state.user.team;
        form.room = state.user.room;
        form.contactEmail = state.user.contactEmail;
        form.contactPhone = state.user.contactPhone;
        form.registration.date = state.user.registration.date;
        form.registration.last_login_date = state.user.registration.last_login_date;
    } catch (error) {
        console.error('Error fetching user', error);
    } finally {
        state.isLoading = false;
    }
});
</script>

<template>
  <section class="bg-green-50">
    <div class="container m-auto max-w-2xl py-24">
      <div
        class="bg-white px-6 py-8 mb-4 shadow-md rounded-md border m-4 md:m-0"
      >
        <!-- v-on -->
        <form @submit.prevent="hanbleSubmit">
          <h2 class="text-3xl text-center font-semibold mb-6">
            Update User
          </h2>

          <div class="mb-4">
            <label class="block text-gray-700 font-bold mb-2">First Name</label>
            <input
              id="first_name"
              v-model="form.first_name"
              type="text"
              name="first_name"
              class="border rounded w-full py-2 px-3 mb-2"
              placeholder="John"
              required
            >
          </div>

          <div class="mb-4">
            <label class="block text-gray-700 font-bold mb-2">Last Name</label>
            <input
              id="last_name"
              v-model="form.last_name"
              type="text"
              name="last_name"
              class="border rounded w-full py-2 px-3 mb-2"
              placeholder="Rembo"
              required
            >
          </div>

          <div class="mb-4">
            <label class="block text-gray-700 font-bold mb-2">NickName</label>
            <input
              id="nickname"
              v-model="form.nickname"
              type="text"
              name="nickname"
              class="border rounded w-full py-2 px-3 mb-2"
              placeholder="nikcname"
              required
            >
          </div>

          <div class="mb-4">
            <label
              for="description"
              class="block text-gray-700 font-bold mb-2"
            >Description</label>
            <textarea
              id="description"
              v-model="form.description"
              name="description"
              class="border rounded w-full py-2 px-3"
              rows="4"
              placeholder="Add any user duties, expectations, requirements, etc"
            />
          </div>

          <div class="mb-4">
            <label
              for="type"
              class="block text-gray-700 font-bold mb-2"
            >Subdivision</label>
            <select
              id="subdivision"
              v-model="form.subdivision"
              name="subdivision"
              class="border rounded w-full py-2 px-3"
              required
            >
              <option value="BHMC">
                BHMC
              </option>
              <option value="IMC">
                IMC
              </option>
              <option value="KJMC">
                KJMC
              </option>
              <option value="Others">
                Others
              </option>
            </select>
          </div>

          <div class="mb-4">
            <label
              for="type"
              class="block text-gray-700 font-bold mb-2"
            >Team</label>
            <select
              id="team"
              v-model="form.team"
              name="team"
              class="border rounded w-full py-2 px-3"
              required
            >
              <option value="Network Group">
                Network Group
              </option>
              <option value="Telecom Group">
                Telecom Group
              </option>
              <option value="Support Group">
                Support Group
              </option>
              <option value="Server Group">
                Server Group
              </option>
              <!-- <option value="$80K - $90K">$80 - $90K</option>
                <option value="$90K - $100K">$90 - $100K</option>
                <option value="$100K - $125K">$100 - $125K</option>
                <option value="$125K - $150K">$125 - $150K</option>
                <option value="$150K - $175K">$150 - $175K</option>
                <option value="$175K - $200K">$175 - $200K</option>
                <option value="Over $200K">Over $200K</option> -->
            </select>
          </div>

          <div class="mb-4">
            <label class="block text-gray-700 font-bold mb-2">
              Room
            </label>
            <input
              id="room"
              v-model="form.room"
              type="text"
              name="room"
              class="border rounded w-full py-2 px-3 mb-2"
              placeholder="User's office location"
              required
            >
          </div>

          <div class="mb-4">
            <label
              for="contact_email"
              class="block text-gray-700 font-bold mb-2"
            >Contact Email</label>
            <input
              id="contact_email"
              v-model="form.contactEmail"
              type="email"
              name="contact_email"
              class="border rounded w-full py-2 px-3"
              placeholder="Email address for user"
              required
            >
          </div>
          <div class="mb-4">
            <label
              for="contact_phone"
              class="block text-gray-700 font-bold mb-2"
            >Contact Phone</label>
            <input
              id="contact_phone"
              v-model="form.contactPhone"
              type="tel"
              name="contact_phone"
              class="border rounded w-full py-2 px-3"
              placeholder="Optional phone for user"
            >
          </div>

          <h3 class="text-2xl mb-5">
            Registration
          </h3>

          <div class="mb-4">
            <label
              for="date"
              class="block text-gray-700 font-bold mb-2"
            >Registration Date</label>
            <input
              id="date"
              v-model="form.registration.date"
              type="text"
              name="date"
              class="border rounded w-full py-2 px-3"
              placeholder="registration date"
            >
          </div>

          <div class="mb-4">
            <label
              for="last_login_date"
              class="block text-gray-700 font-bold mb-2"
            >Last Login Date</label>
            <textarea
              id="last_login_date"
              v-model="form.registration.last_login_date"
              name="last_login_date"
              class="border rounded w-full py-2 px-3"
              rows="4"
              placeholder="What time has the user been login"
            />
          </div>

          <div>
            <button
              class="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-4 rounded-full w-full focus:outline-none focus:shadow-outline"
              type="submit"
            >
              Update User
            </button>
          </div>
        </form>
      </div>
    </div>
  </section>
</template>