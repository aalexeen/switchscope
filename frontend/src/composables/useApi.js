import { inject } from 'vue';

export function useApi() {
    const $api = inject('$api');
    if (!$api) {
        throw new Error("API plugin is not installed! Make sure to call `app.use(ApiPlugin)` in `main.js`.");
    }
    return $api;
}