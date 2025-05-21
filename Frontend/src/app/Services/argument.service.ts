import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ArgumentRequest {
  speaker: string;
  text:     string;
}

export interface Decision {
  speaker:     string;
  text:        string;
  willWork:       string;
  reason?:  string;
}

export interface SummaryResponse {
  totalArguments: number;
  summary : string;
}

@Injectable({ providedIn: 'root' })
export class ArgumentService {
  private base = '/api';

  constructor(private http: HttpClient) {}

  evalOne(req: ArgumentRequest): Observable<Decision> {
    return this.http.post<Decision>(`${this.base}/argument`, req);
  }

  evalBatch(reqs: ArgumentRequest[]): Observable<Decision[]> {
    return this.http.post<Decision[]>(`${this.base}/arguments`, reqs);
  }

  summaryBatch(reqs: ArgumentRequest[]): Observable<SummaryResponse> {
    return this.http.post<SummaryResponse>(`${this.base}/arguments/summary`, reqs);
  }
}
