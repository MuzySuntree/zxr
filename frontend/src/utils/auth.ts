export interface LoginUser { userId: number; username: string; realName: string; gender: number; role: number; token: string }

export const getLoginUser = (): LoginUser | null => {
  const raw = localStorage.getItem('loginUser')
  return raw ? JSON.parse(raw) : null
}

export const setLoginUser = (user: LoginUser) => localStorage.setItem('loginUser', JSON.stringify(user))
export const clearLoginUser = () => localStorage.removeItem('loginUser')
