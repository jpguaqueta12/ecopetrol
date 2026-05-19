import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VacationLeaderRequestsService } from 'src/app/core/services/vacation-leader-requests.service';

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.css']
})
export class DetailComponent implements OnInit {
  isSidebarClosed: boolean = false;
  requestData: any = null;
  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: VacationLeaderRequestsService
  ) { }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.isLoading = true;
    this.service.getRequests().subscribe({
      next: (items) => {
        this.requestData = items.find(r => r.id === id) || null;
        this.isLoading = false;
        if (!this.requestData) this.errorMessage = 'Solicitud no encontrada.';
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
    this.router.navigate(['/vacation-leader']);
  }

  onApprove(): void {
    if (!this.requestData) return;
    this.service.approve(this.requestData.id).subscribe({
      next: () => { this.requestData.status = 'Aprobado'; },
      error: (err) => { this.errorMessage = 'Error al aprobar: ' + err; }
    });
  }

  onReject(): void {
    if (!this.requestData) return;
    this.service.reject(this.requestData.id).subscribe({
      next: () => { this.requestData.status = 'Rechazado'; },
      error: (err) => { this.errorMessage = 'Error al rechazar: ' + err; }
    });
  }
}
