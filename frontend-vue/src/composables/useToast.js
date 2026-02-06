import { reactive } from 'vue'

const toastState = reactive({
  visible: false,
  message: '',
  type: 'info',
  timeoutId: null,
})

const showToast = (message, type = 'info', duration = 4000) => {
  toastState.message = message
  toastState.type = type
  toastState.visible = true

  if (toastState.timeoutId) {
    clearTimeout(toastState.timeoutId)
  }

  toastState.timeoutId = setTimeout(() => {
    toastState.visible = false
  }, duration)
}

const hideToast = () => {
  toastState.visible = false
  if (toastState.timeoutId) {
    clearTimeout(toastState.timeoutId)
    toastState.timeoutId = null
  }
}

export const useToast = () => ({
  toastState,
  showToast,
  hideToast,
})
