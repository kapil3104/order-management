import { render, screen } from '@testing-library/react';
import App from './App';

test('Testing link', () => {
  render(<App />);
  const linkElement = screen.getByText(/Testing Now/i);
  expect(linkElement).toBeInTheDocument();
});
