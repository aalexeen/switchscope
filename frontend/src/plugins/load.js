export default {
    install(Vue) {
        Vue.config.globalProperties.$load = async (action, errHandler, bool) => {
            try{
                await action();
            } catch (error) {
                if (errHandler) {
                    errHandler(error);
                } else {
                    console.log(error);
                }
            } finally {
                state.isLoading = bool;
            }
        };
    }
}