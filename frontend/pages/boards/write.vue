<template>
  <div class="container">
    <div class="nav-bar">
      <NuxtLink to="/" class="btn-back">← 목록으로 돌아가기</NuxtLink>
    </div>

    <section class="card form-card">
      <div class="card-header">신규 게시글 작성</div>
      <form @submit.prevent="createBoard" class="form-body">
        <div class="input-row">
          <div class="input-group">
            <label>작성자</label>
            <input v-model="form.nickname" type="text" placeholder="작성자명" required />
          </div>
          <div class="input-group">
            <label>비밀번호</label>
            <input v-model="form.password" type="password" placeholder="비밀번호" required />
          </div>
        </div>

        <div class="input-group">
          <label>제목</label>
          <input v-model="form.title" type="text" placeholder="제목을 입력하세요" required />
        </div>

        <div class="input-group">
          <label>내용</label>
          <textarea v-model="form.content" rows="10" placeholder="내용을 구체적으로 작성하세요" required></textarea>
        </div>

        <div class="form-actions">
          <NuxtLink to="/" class="btn btn-secondary">취소</NuxtLink>
          <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
            {{ isSubmitting ? '저장 중...' : '등록 완료' }}
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'

const router = useRouter()
const isSubmitting = ref(false)

const form = reactive({
  title: '',
  content: '',
  nickname: '',
  password: ''
})

const createBoard = async () => {
  isSubmitting.value = true
  try {
    await $fetch('http://localhost:8080/api/boards', {
      method: 'POST',
      body: {
        title: form.title,
        content: form.content,
        nickname: form.nickname,
        password: form.password
      }
    })
    alert('등록되었습니다.')
    router.push('/') // 등록 후 메인 목록으로 이동
  } catch (err) {
    alert('등록 실패: ' + (err.data?.message || err.message))
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.container {
  max-width: 860px;
  margin: 30px auto;
  padding: 0 16px;
}

.nav-bar {
  margin-bottom: 16px;
}

.btn-back {
  font-size: 13px;
  color: #475569;
  text-decoration: none;
  font-weight: 500;
}

.btn-back:hover {
  color: #2563eb;
}

.card {
  background: #ffffff;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.card-header {
  background-color: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  padding: 14px 20px;
  font-size: 15px;
  font-weight: 600;
  color: #334155;
}

.form-body {
  padding: 24px;
}

.input-row {
  display: flex;
  gap: 16px;
}

.input-group {
  margin-bottom: 16px;
  flex: 1;
}

.input-group label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 6px;
}

input, textarea {
  width: 100%;
  box-sizing: border-box;
  padding: 10px 12px;
  font-size: 13px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  outline: none;
  background-color: #fff;
}

input:focus, textarea:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 1px #2563eb;
}

textarea {
  resize: vertical;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 16px;
}
</style>