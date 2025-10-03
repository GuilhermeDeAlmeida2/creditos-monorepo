import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ApiService, PingResponse, Credito, PaginatedCreditoResponse } from './api.service';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService]
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('ping', () => {
    it('should return ping response', () => {
      const mockResponse: PingResponse = {
        message: 'pong',
        ts: '2025-10-03T00:58:09.731155Z'
      };

      service.ping().subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/ping');
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should handle ping error', () => {
      service.ping().subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });

      const req = httpMock.expectOne('http://localhost:8080/api/ping');
      req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('buscarCreditosPorNfse', () => {
    it('should return paginated creditos response', () => {
      const mockResponse: PaginatedCreditoResponse = {
        content: [
          {
            id: 1,
            numeroCredito: '123456',
            numeroNfse: '7891011',
            dataConstituicao: '2024-02-25',
            valorIssqn: 1500.75,
            tipoCredito: 'ISSQN',
            simplesNacional: true,
            aliquota: 5.00,
            valorFaturado: 30000.00,
            valorDeducao: 5000.00,
            baseCalculo: 25000.00
          }
        ],
        page: 0,
        size: 10,
        totalElements: 1,
        totalPages: 1,
        first: true,
        last: true,
        hasNext: false,
        hasPrevious: false
      };

      service.buscarCreditosPorNfse('7891011', 0, 10).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/creditos/paginated/7891011?page=0&size=10');
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should use default page and size parameters', () => {
      const mockResponse: PaginatedCreditoResponse = {
        content: [],
        page: 0,
        size: 10,
        totalElements: 0,
        totalPages: 0,
        first: true,
        last: true,
        hasNext: false,
        hasPrevious: false
      };

      service.buscarCreditosPorNfse('7891011').subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/creditos/paginated/7891011?page=0&size=10');
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should handle 404 error for non-existent NFS-e', () => {
      service.buscarCreditosPorNfse('9999999').subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(404);
        }
      });

      const req = httpMock.expectOne('http://localhost:8080/api/creditos/paginated/9999999?page=0&size=10');
      req.flush('Not Found', { status: 404, statusText: 'Not Found' });
    });

    it('should handle server error', () => {
      service.buscarCreditosPorNfse('7891011').subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });

      const req = httpMock.expectOne('http://localhost:8080/api/creditos/paginated/7891011?page=0&size=10');
      req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });
    });
  });
});
