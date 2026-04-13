export interface LoginUser {
  userId: number
  username: string
  realName?: string
  gender?: number
  role: number
  token?: string
}

const USER_KEY = 'loginUser'

export const getLoginUser = (): LoginUser | null => {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch {
    return null
  }
}

export const setLoginUser = (user: LoginUser) => {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export const clearLoginUser = () => {
  localStorage.removeItem(USER_KEY)
}

export const isAdmin = () => getLoginUser()?.role === 1
