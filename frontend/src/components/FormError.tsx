import './FormError.css'

function FormError({ message }: { message: string | null }) {
  if (!message) return null
  return <div className="form-error">{message}</div>
}

export default FormError