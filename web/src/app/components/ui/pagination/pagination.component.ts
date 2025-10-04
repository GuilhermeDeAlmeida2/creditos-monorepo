import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface PaginationInfo {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css'],
})
export class PaginationComponent implements OnChanges {
  @Input() paginationInfo: PaginationInfo | null = null;
  @Input() showInfo: boolean = true;
  @Input() showPageSizeSelector: boolean = true;
  @Input() pageSizeOptions: number[] = [5, 10, 20, 50];
  @Input() maxVisiblePages: number = 5;
  @Input() compact: boolean = false;

  @Output() pageChange = new EventEmitter<number>();
  @Output() pageSizeChange = new EventEmitter<number>();

  selectedPageSize: number = 10;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['paginationInfo'] && this.paginationInfo) {
      this.selectedPageSize = this.paginationInfo.size;
    }
  }

  getPageNumbers(): number[] {
    if (!this.paginationInfo) return [];

    const { page, totalPages } = this.paginationInfo;
    const pages: number[] = [];

    if (totalPages <= this.maxVisiblePages) {
      // Mostrar todas as páginas se o total for menor que o máximo
      for (let i = 0; i < totalPages; i++) {
        pages.push(i);
      }
    } else {
      // Calcular páginas visíveis
      const halfVisible = Math.floor(this.maxVisiblePages / 2);
      let startPage = Math.max(0, page - halfVisible);
      const endPage = Math.min(totalPages - 1, startPage + this.maxVisiblePages - 1);

      // Ajustar se estivermos muito próximos do final
      if (endPage - startPage < this.maxVisiblePages - 1) {
        startPage = Math.max(0, endPage - this.maxVisiblePages + 1);
      }

      for (let i = startPage; i <= endPage; i++) {
        pages.push(i);
      }
    }

    return pages;
  }

  goToPage(page: number): void {
    if (this.paginationInfo && page >= 0 && page < this.paginationInfo.totalPages) {
      this.pageChange.emit(page);
    }
  }

  goToFirstPage(): void {
    this.goToPage(0);
  }

  goToLastPage(): void {
    if (this.paginationInfo) {
      this.goToPage(this.paginationInfo.totalPages - 1);
    }
  }

  goToPreviousPage(): void {
    if (this.paginationInfo && this.paginationInfo.hasPrevious) {
      this.goToPage(this.paginationInfo.page - 1);
    }
  }

  goToNextPage(): void {
    if (this.paginationInfo && this.paginationInfo.hasNext) {
      this.goToPage(this.paginationInfo.page + 1);
    }
  }

  onPageSizeChange(): void {
    this.pageSizeChange.emit(this.selectedPageSize);
  }

  getDisplayPageNumber(page: number): number {
    return page + 1; // Converter de índice 0-based para 1-based para exibição
  }

  getTotalElements(): number {
    return this.paginationInfo?.totalElements || 0;
  }

  getCurrentPage(): number {
    return this.paginationInfo ? this.paginationInfo.page + 1 : 0;
  }

  getTotalPages(): number {
    return this.paginationInfo?.totalPages || 0;
  }

  getPageSize(): number {
    return this.paginationInfo?.size || 0;
  }

  getStartElement(): number {
    if (!this.paginationInfo) return 0;
    return this.paginationInfo.page * this.paginationInfo.size + 1;
  }

  getEndElement(): number {
    if (!this.paginationInfo) return 0;
    const end = this.paginationInfo.page * this.paginationInfo.size + this.paginationInfo.size;
    return Math.min(end, this.paginationInfo.totalElements);
  }
}
