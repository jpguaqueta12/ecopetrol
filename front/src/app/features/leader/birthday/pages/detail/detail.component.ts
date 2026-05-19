import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BirthdayLeaderRequestsService } from 'src/app/core/services/birthday-leader-requests.service';

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.css']
})
export class DetailComponent implements OnInit {
  isSidebarClosed: boolean = false;
  detailData: any = null;
  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: BirthdayLeaderRequestsService
  ) { }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.isLoading = true;
    this.service.getRequests().subscribe({
      next: (items) => {
        this.detailData = items.find(r => r.id === id) || null;
        this.isLoading = false;
        if (!this.detailData) this.errorMessage = 'Solicitud no encontrada.';
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar la solicitud: ' + err;
        this.isLoading = false;
      }
    });
  }

  toggleSidebar(): void {
    this.isSidebarClosed = !this.isSidebarClosed;
  }

  onBack(): void {
    this.router.navigate(['/birthday-leader']);
  }

  onApprove(): void {
    if (!this.detailData) return;
    this.service.approve(this.detailData.id).subscribe({
      next: () => { this.detailData.status = 'Aprobado'; },
      error: (err) => { this.errorMessage = 'Error al aprobar: ' + err; }
    });
  }

  onReject(): void {
    if (!this.detailData) return;
    this.service.reject(this.detailData.id).subscribe({
      next: () => { this.detailData.status = 'Rechazado'; },
      error: (err) => { this.errorMessage = 'Error al rechazar: ' + err; }
    });
  }
}
