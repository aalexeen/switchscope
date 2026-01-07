// api - 4th step
import api from '@/api/index';

export default {
    install(app) {
        app.provide('$api', api);
    }
}