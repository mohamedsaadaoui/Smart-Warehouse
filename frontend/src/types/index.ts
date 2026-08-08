export interface AuthResponse {
  accessToken: string
  tokenType: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  firstName: string
  lastName: string
  email: string
  password: string
  phoneNumber?: string
}

export interface Category {
  id: string
  name: string
  description: string | null
  active: boolean
  createdAt: string
  updatedAt: string
}

export interface CategoryRequest {
  name: string
  description?: string
  active?: boolean
}

export interface Page<T> {
  content: T[]
  totalPages: number
  totalElements: number
  number: number
  size: number
  first: boolean
  last: boolean
  empty: boolean
}

export type ProductStatus = 'IN_STOCK' | 'LOW_STOCK' | 'OUT_OF_STOCK'

export interface Product {
  id: string
  name: string
  sku: string
  description: string | null
  price: number
  quantity: number
  minStock: number
  status: ProductStatus
  categoryId: string
  categoryName: string
  active: boolean
  createdAt: string
  updatedAt: string
}

export interface ProductRequest {
  name: string
  sku: string
  description?: string
  price: number
  quantity: number
  minStock: number
  categoryId: string
  active?: boolean
}

export interface LowStockProduct {
  id: string
  name: string
  sku: string
  quantity: number
  minStock: number
}

export interface CategoryProductCount {
  name: string
  productCount: number
}

export interface DashboardSummary {
  totalProducts: number
  totalCategories: number
  lowStockCount: number
  outOfStockCount: number
  lowStockProducts: LowStockProduct[]
  productsPerCategory: CategoryProductCount[]
}
