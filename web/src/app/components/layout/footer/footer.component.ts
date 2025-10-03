import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css'],
})
export class FooterComponent {
  @Input() companyName: string = 'Sistema de Créditos Constituídos';
  @Input() version: string = '1.0.0';
  @Input() currentYear: number = new Date().getFullYear();
  @Input() showVersion: boolean = true;
  @Input() showCopyright: boolean = true;
}
