import { Component, OnDestroy, OnInit } from '@angular/core';
import { SnackbarService } from '../../services/snackbar.service';

@Component({
  selector: 'app-snackbar',
  templateUrl: './snackbar.component.html',
  styleUrls: ['./snackbar.component.css']
})
export class SnackbarComponent implements OnInit, OnDestroy {
  visible = false;
  message = '';
  type: 'success' | 'error' = 'success';
  private timeoutId: any;

  constructor(private snackbarService: SnackbarService) {}

  ngOnInit(): void {
    this.snackbarService.registerListener((config) => {
      this.message = config.message;
      this.type = config.type;
      this.visible = true;

      if (this.timeoutId) {
        clearTimeout(this.timeoutId);
      }
      this.timeoutId = setTimeout(() => {
        this.visible = false;
      }, config.type === 'success' ? 3000 : 5000);
    });
  }

  ngOnDestroy(): void {
    if (this.timeoutId) {
      clearTimeout(this.timeoutId);
    }
  }
}
