import api from '../lib/api'
import type {
  BookRequest,
  BookResponse,
} from '../types/book'

export const createBook = async (
  data: BookRequest,
): Promise<BookResponse> => {
  const response = await api.post<BookResponse>('/books', data)
  return response.data
}

export const searchBooks = async (
  title: string,
): Promise<BookResponse[]> => {
  const response = await api.get<BookResponse[]>('/books/search', {
    params: { title },
  })

  return response.data
}