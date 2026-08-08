import { useEffect, useState } from 'react'
import { Grid, Card, CardContent, CardHeader, Typography, Box } from '@mui/material'
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
} from 'recharts'
import Inventory2OutlinedIcon from '@mui/icons-material/Inventory2Outlined'
import WarningAmberOutlinedIcon from '@mui/icons-material/WarningAmberOutlined'
import ReceiptLongOutlinedIcon from '@mui/icons-material/ReceiptLongOutlined'
import CategoryOutlinedIcon from '@mui/icons-material/CategoryOutlined'
import { useAuth } from '../context/AuthContext'
import { categoryApi } from '../api/categoryApi'
import Loading from '../components/common/Loading'

const INVENTORY_DATA = [
  { month: 'Jan', inbound: 420, outbound: 380 },
  { month: 'Feb', inbound: 510, outbound: 430 },
  { month: 'Mar', inbound: 470, outbound: 520 },
  { month: 'Apr', inbound: 590, outbound: 480 },
  { month: 'May', inbound: 640, outbound: 560 },
  { month: 'Jun', inbound: 610, outbound: 620 },
  { month: 'Jul', inbound: 720, outbound: 590 },
]

interface StatCardProps {
  label: string
  value: number
  icon: React.ReactNode
  color: string
}

function StatCard({ label, value, icon, color }: StatCardProps) {
  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Box
            sx={{
              width: 48,
              height: 48,
              borderRadius: 2,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              bgcolor: color,
              color: 'white',
            }}
          >
            {icon}
          </Box>
          <Box>
            <Typography variant="h5" fontWeight={700}>
              {value}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {label}
            </Typography>
          </Box>
        </Box>
      </CardContent>
    </Card>
  )
}

export default function Dashboard() {
  const { email } = useAuth()
  const [categories, setCategories] = useState(0)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    categoryApi
      .getAll({ page: 0, size: 1 })
      .then((page) => setCategories(page.totalElements))
      .catch(() => setCategories(0))
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return <Loading />
  }

  const firstName = email.split('@')[0].replace(/[._-]/g, ' ')

  return (
    <Box>
      <Typography variant="h5" fontWeight={600} gutterBottom>
        Good morning, {firstName}
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Here is an overview of your warehouse today
      </Typography>

      <Grid container spacing={3}>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            label="Products"
            value={1248}
            icon={<Inventory2OutlinedIcon />}
            color="#4f46e5"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            label="Low Stock"
            value={24}
            icon={<WarningAmberOutlinedIcon />}
            color="#e11d48"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            label="Orders"
            value={87}
            icon={<ReceiptLongOutlinedIcon />}
            color="#059669"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            label="Categories"
            value={categories}
            icon={<CategoryOutlinedIcon />}
            color="#d97706"
          />
        </Grid>
      </Grid>

      <Card sx={{ mt: 3 }}>
        <CardHeader
          title="Inventory Overview"
          subheader="Inbound vs outbound movements"
        />
        <CardContent>
          <ResponsiveContainer width="100%" height={320}>
            <AreaChart data={INVENTORY_DATA} margin={{ top: 8, right: 24, left: 0, bottom: 0 }}>
              <defs>
                <linearGradient id="colorInbound" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#4f46e5" stopOpacity={0.35} />
                  <stop offset="95%" stopColor="#4f46e5" stopOpacity={0} />
                </linearGradient>
                <linearGradient id="colorOutbound" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#059669" stopOpacity={0.35} />
                  <stop offset="95%" stopColor="#059669" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="month" tickLine={false} axisLine={false} />
              <YAxis tickLine={false} axisLine={false} width={40} />
              <Tooltip />
              <Area
                type="monotone"
                dataKey="inbound"
                name="Inbound"
                stroke="#4f46e5"
                fill="url(#colorInbound)"
                strokeWidth={2}
              />
              <Area
                type="monotone"
                dataKey="outbound"
                name="Outbound"
                stroke="#059669"
                fill="url(#colorOutbound)"
                strokeWidth={2}
              />
            </AreaChart>
          </ResponsiveContainer>
        </CardContent>
      </Card>
    </Box>
  )
}
