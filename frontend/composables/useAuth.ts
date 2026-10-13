export interface AuthUser {
  id: number
  username: string
  nickname: string
}

export const useAuth = () => {
  const user = useState<AuthUser | null>('auth_user', () => null)

  // 새로고침 시 localStorage에서 로그인 정보 복원
  const initAuth = () => {
    if (import.meta.client) {
      const saved = localStorage.getItem('auth_user')
      if (saved) {
        try {
          user.value = JSON.parse(saved)
        } catch {
          localStorage.removeItem('auth_user')
        }
      }
    }
  }

  // 로그인 처리
  const loginUser = (userData: AuthUser) => {
    user.value = userData
    if (import.meta.client) {
      localStorage.setItem('auth_user', JSON.stringify(userData))
    }
  }

  // 로그아웃 처리
  const logoutUser = () => {
    user.value = null
    if (import.meta.client) {
      localStorage.removeItem('auth_user')
    }
  }

  return {
    user,
    initAuth,
    loginUser,
    logoutUser,
  }
}