import { useCallback, useEffect, useState } from 'react'
import {
  Badge,
  Box,
  Chip,
  Divider,
  IconButton,
  List,
  ListItemButton,
  ListItemText,
  Menu,
  Typography,
} from '@mui/material'
import NotificationsNoneOutlinedIcon from '@mui/icons-material/NotificationsNoneOutlined'
import { useNavigate } from 'react-router-dom'
import { notificationApi } from '../../api/notificationApi'
import type { StockAlert } from '../../types'

const POLL_INTERVAL_MS = 30_000

export default function NotificationBell() {
  const navigate = useNavigate()
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null)
  const [count, setCount] = useState(0)
  const [alerts, setAlerts] = useState<StockAlert[]>([])

  const refresh = useCallback(async () => {
    try {
      const [alertCount, alertList] = await Promise.all([
        notificationApi.getAlertCount(),
        notificationApi.getAlerts(),
      ])
      setCount(alertCount)
      setAlerts(alertList)
    } catch {
      // ignore polling errors
    }
  }, [])

  useEffect(() => {
    refresh()
    const interval = setInterval(refresh, POLL_INTERVAL_MS)
    return () => clearInterval(interval)
  }, [refresh])

  const handleOpen = (e: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(e.currentTarget)
    refresh()
  }

  const handleClose = () => setAnchorEl(null)

  const openProduct = () => {
    handleClose()
    navigate('/products')
  }

  return (
    <>
      <IconButton color="default" onClick={handleOpen}>
        <Badge badgeContent={count} color="error">
          <NotificationsNoneOutlinedIcon />
        </Badge>
      </IconButton>
      <Menu
        anchorEl={anchorEl}
        open={Boolean(anchorEl)}
        onClose={handleClose}
        slotProps={{ paper: { sx: { width: 360, maxHeight: 420, mt: 1 } } }}
      >
        <Box sx={{ px: 2, py: 1.5 }}>
          <Typography variant="subtitle1" fontWeight={600}>
            Notifications
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Low stock alerts
          </Typography>
        </Box>
        <Divider />
        {alerts.length === 0 ? (
          <Box sx={{ px: 2, py: 3, textAlign: 'center' }}>
            <Typography variant="body2" color="text.secondary">
              No low stock alerts right now
            </Typography>
          </Box>
        ) : (
          <List dense disablePadding>
            {alerts.map((alert) => (
              <ListItemButton
                key={alert.productId}
                onClick={openProduct}
                sx={{ alignItems: 'flex-start', px: 2, py: 1 }}
              >
                <ListItemText
                  primary={
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', gap: 1 }}>
                      <Typography variant="body2" fontWeight={500} noWrap>
                        {alert.productName}
                      </Typography>
                      <Chip
                        label={alert.type === 'OUT_OF_STOCK' ? 'Out of stock' : 'Low stock'}
                        color={alert.type === 'OUT_OF_STOCK' ? 'error' : 'warning'}
                        size="small"
                      />
                    </Box>
                  }
                  secondary={
                    <Typography variant="caption" color="text.secondary">
                      {alert.sku} — {alert.quantity} left
                      {alert.quantity <= alert.minStock ? ` (min ${alert.minStock})` : ''}
                    </Typography>
                  }
                />
              </ListItemButton>
            ))}
          </List>
        )}
      </Menu>
    </>
  )
}
