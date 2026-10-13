<script setup>
const router = useRouter()
const { loginUser } = useAuth()

const isSignup = ref(false)

const form = ref({
  username: '',
  password: '',
  nickname: '',
})

const errorMessage = ref('')

const toggleMode = () => {
  isSignup.value = !isSignup.value
  errorMessage.value = ''
  form.value = { username: '', password: '', nickname: '' }
}

const handleSubmit = async () => {
  errorMessage.value = ''

  if (!form.value.username || !form.value.password) {
    errorMessage.value = '아이디와 비밀번호를 모두 입력해주세요.'
    return
  }

  if (isSignup.value && !form.value.nickname) {
    errorMessage.value = '닉네임을 입력해주세요.'
    return
  }

  const endpoint = isSignup.value
    ? 'http://localhost:8080/api/users/signup'
    : 'http://localhost:8080/api/users/login'

  try {
    const res = await $fetch(endpoint, {
      method: 'POST',
      body: isSignup.value
        ? form.value
        : { username: form.value.username, password: form.value.password },
    })

    if (isSignup.value) {
      alert('회원가입이 완료되었습니다. 로그인해주세요!')
      isSignup.value = false
      form.value.password = ''
    } else {
      // 로그인 성공: 전역 상태 저장 후 메인 이동
      loginUser(res)
      router.push('/')
    }
  } catch (err) {
    errorMessage.value = err?.data?.message || err?.message || '요청 처리 중 오류가 발생했습니다.'
  }
}
</script>

<template>
  <div style="max-width: 400px; margin: 60px auto; padding: 24px; border: 1px solid #e2e8f0; border-radius: 8px;">
    <h2 style="margin-bottom: 20px; text-align: center;">
      {{ isSignup ? '회원가입' : '로그인' }}
    </h2>

    <form @submit.prevent="handleSubmit" style="display: flex; flexDirection: column; gap: 14px;">
      <div>
        <label style="display: block; font-size: 13px; margin-bottom: 4px;">아이디</label>
        <input
          v-model="form.username"
          type="text"
          placeholder="아이디 입력"
          style="width: 100%; padding: 8px; border: 1px solid #cbd5e1; border-radius: 4px; box-sizing: border-box;"
        />
      </div>

      <div>
        <label style="display: block; font-size: 13px; margin-bottom: 4px;">비밀번호</label>
        <input
          v-model="form.password"
          type="password"
          placeholder="비밀번호 입력"
          style="width: 100%; padding: 8px; border: 1px solid #cbd5e1; border-radius: 4px; box-sizing: border-box;"
        />
      </div>

      <div v-if="isSignup">
        <label style="display: block; font-size: 13px; margin-bottom: 4px;">닉네임</label>
        <input
          v-model="form.nickname"
          type="text"
          placeholder="게시판에 표시될 닉네임"
          style="width: 100%; padding: 8px; border: 1px solid #cbd5e1; border-radius: 4px; box-sizing: border-box;"
        />
      </div>

      <p v-if="errorMessage" style="color: #ef4444; font-size: 13px; margin: 0;">
        {{ errorMessage }}
      </p>

      <button
        type="submit"
        style="padding: 10px; background: #2563eb; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;"
      >
        {{ isSignup ? '가입하기' : '로그인' }}
      </button>
    </form>

    <div style="margin-top: 16px; text-align: center; font-size: 13px;">
      <span style="color: #64748b;">
        {{ isSignup ? '이미 계정이 있으신가요?' : '아직 계정이 없으신가요?' }}
      </span>
      <button
        @click="toggleMode"
        style="margin-left: 6px; background: none; border: none; color: #2563eb; cursor: pointer; text-decoration: underline;"
      >
        {{ isSignup ? '로그인하러 가기' : '회원가입' }}
      </button>
    </div>
  </div>
</template>