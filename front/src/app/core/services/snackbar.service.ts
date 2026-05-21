import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SnackbarService {
  private listener?: (config: { message: string; type: 'success' | 'error' }) => void;

  registerListener(fn: (config: { message: string; type: 'success' | 'error' }) => void) {
    this.listener = fn;
  }

  showSuccess(message: string) {
    this.listener?.({ message, type: 'success' });
  }

  showError(message: string) {
    this.listener?.({ message, type: 'error' });
  }
}
