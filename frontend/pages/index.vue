<template>
  <div class="container">
    <!-- 헤더 영역 -->
    <header class="header">
      <div class="title-group">
        <h2>공지 및 작업 게시판</h2>
        <span class="sub-text">사내 작업 요청 및 공정 이슈 익명 공유 보드</span>
      </div>
      <div class="stats-badge">
        총 등록 건수: <strong>{{ boards ? boards.length : 0 }}</strong>건
      </div>
    </header>

    <!-- 신규 게시글 등록 섹션 -->
    <section class="card form-card">
      <div class="card-header">신규 등록</div>
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
          <textarea v-model="form.content" rows="3" placeholder="내용을 구체적으로 작성하세요" required></textarea>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
            {{ isSubmitting ? '저장 중...' : '등록' }}
          </button>
        </div>
      </form>
    </section>

    <!-- 게시글 목록 그리드 -->
    <section class="card table-card">
      <div class="card-header flex-between">
        <span>게시글 내역</span>
        <button @click="refresh" class="btn btn-secondary btn-sm">새로고침</button>
      </div>

      <div v-if="pending" class="status-msg">데이터를 동기화하는 중입니다...</div>
      <div v-else-if="error" class="status-msg error-msg">
        데이터 조회 실패: {{ error.message }}
      </div>

      <table v-else class="data-table">
        <thead>
          <tr>
            <th class="w-num">No.</th>
            <th>제목</th>
            <th class="w-author">작성자</th>
            <th class="w-date">등록일시</th>
            <th class="w-action">관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(board, index) in boards" :key="board.id">
            <!-- DB id 대신 전체 개수에서 인덱스를 뺀 가상 순번 출력 -->
            <td class="text-center font-mono">{{ index + 1 }}</td>
            <td>
              <NuxtLink :to="`/boards/${board.id}`" class="post-title">
                {{ board.title }}
              </NuxtLink>
            </td>
            <td class="text-center">{{ board.nickname }}</td>
            <td class="text-center font-mono text-muted">
              {{ board.createdAt ? board.createdAt.substring(0, 10) : '-' }}
            </td>
            <td class="text-center">
              <button @click="deleteBoard(board.id)" class="btn btn-danger btn-sm">삭제</button>
            </td>
          </tr>
          <tr v-if="!boards || boards.length === 0">
            <td colspan="5" class="empty-cell">등록된 데이터가 존재하지 않습니다.</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'

const isSubmitting = ref(false)

const form = reactive({
  title: '',
  content: '',
  nickname: '',
  password: ''
})

const { data: boards, pending, error, refresh } = await useFetch('http://localhost:8080/api/boards')

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
    form.title = ''
    form.content = ''
    form.nickname = ''
    form.password = ''
    await refresh()
  } catch (err) {
    alert('등록 실패: ' + (err.data?.message || err.message))
  } finally {
    isSubmitting.value = false
  }
}

const deleteBoard = async (id) => {
  const password = prompt('삭제 비밀번호를 입력해주세요:')
  if (!password) return

  try {
    await $fetch(`http://localhost:8080/api/boards/${id}`, {
      method: 'DELETE',
      body: { password }
    })
    await refresh()
  } catch (err) {
    alert('삭제 실패: ' + (err.data?.message || '비밀번호를 확인해주세요.'))
  }
}
</script>

<style scoped>
.container {
  max-width: 960px;
  margin: 30px auto;
  padding: 0 16px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  border-bottom: 2px solid #2563eb;
  padding-bottom: 12px;
  margin-bottom: 24px;
}

.title-group h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #0f172a;
}

.sub-text {
  font-size: 13px;
  color: #64748b;
  margin-top: 4px;
  display: inline-block;
}

.stats-badge {
  font-size: 13px;
  background-color: #f1f5f9;
  padding: 6px 12px;
  border-radius: 4px;
  border: 1px solid #e2e8f0;
}

.card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.card-header {
  background-color: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  padding: 10px 16px;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
}

.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-body {
  padding: 16px;
}

.input-row {
  display: flex;
  gap: 16px;
}

.input-group {
  margin-bottom: 12px;
  flex: 1;
}

.input-group label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 4px;
}

input, textarea {
  width: 100%;
  box-sizing: border-box;
  padding: 8px 10px;
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
  margin-top: 8px;
}

/* 데이터 테이블 */
.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.data-table th, .data-table td {
  padding: 10px 12px;
  border-bottom: 1px solid #e2e8f0;
}

.data-table th {
  background-color: #f8fafc;
  color: #475569;
  font-weight: 600;
  text-align: left;
}

.data-table tbody tr:hover {
  background-color: #f8fafc;
}

.w-num { width: 60px; text-align: center; }
.w-author { width: 120px; text-align: center; }
.w-date { width: 120px; text-align: center; }
.w-action { width: 80px; text-align: center; }

.text-center { text-align: center; }
.text-muted { color: #64748b; }

.post-title {
  color: #0f172a;
  text-decoration: none;
  font-weight: 500;
}

.post-title:hover {
  color: #2563eb;
  text-decoration: underline;
}

.empty-cell {
  text-align: center;
  padding: 30px;
  color: #94a3b8;
}

.status-msg {
  padding: 20px;
  text-align: center;
  font-size: 13px;
  color: #64748b;
}

.error-msg {
  color: #ef4444;
}
</style>